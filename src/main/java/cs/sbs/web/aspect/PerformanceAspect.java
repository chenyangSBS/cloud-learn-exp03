package cs.sbs.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class PerformanceAspect {

    private final ConcurrentHashMap<String, MethodStats> statsMap = new ConcurrentHashMap<>();

    @Pointcut("execution(* cs.sbs.web.service.*.*(..)) || execution(* cs.sbs.web.dao.*.*(..))")
    public void monitoredMethods() {
    }

    @Around("monitoredMethods()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Instant start = Instant.now();

        System.out.printf("⏱️  开始执行: %s%n", methodName);

        try {
            Object result = joinPoint.proceed();

            Instant end = Instant.now();
            long duration = Duration.between(start, end).toMillis();

            updateStats(methodName, duration, true);

            if (duration > 100) {
                System.out.printf("⚠️  性能警告: %s 执行耗时 %d ms（超过100ms）%n", methodName, duration);
            } else {
                System.out.printf("✅ 执行完成: %s 耗时 %d ms%n", methodName, duration);
            }

            return result;
        } catch (Exception e) {
            Instant end = Instant.now();
            long duration = Duration.between(start, end).toMillis();
            updateStats(methodName, duration, false);

            System.err.printf("❌ 执行失败: %s 耗时 %d ms, 异常: %s%n", methodName, duration, e.getMessage());
            throw e;
        }
    }

    private void updateStats(String methodName, long duration, boolean success) {
        statsMap.computeIfAbsent(methodName, k -> new MethodStats())
                .addExecution(duration, success);
    }

    public void printStatsReport() {
        System.out.println("\n📊 ========== 性能统计报告 ==========");
        System.out.printf("%-40s %10s %10s %10s %10s%n", "方法", "调用次数", "平均耗时", "最大耗时", "成功率");
        System.out.println("-".repeat(85));

        statsMap.forEach((method, stats) -> System.out.printf(
                "%-40s %10d %10.2f %10d %9.1f%%%n",
                method,
                stats.getCount(),
                stats.getAverageTime(),
                stats.getMaxTime(),
                stats.getSuccessRate() * 100
        ));
        System.out.println("=====================================\n");
    }

    private static class MethodStats {
        private final AtomicLong count = new AtomicLong(0);
        private final AtomicLong totalTime = new AtomicLong(0);
        private final AtomicLong maxTime = new AtomicLong(0);
        private final AtomicLong successCount = new AtomicLong(0);

        void addExecution(long duration, boolean success) {
            count.incrementAndGet();
            totalTime.addAndGet(duration);

            long currentMax;
            do {
                currentMax = maxTime.get();
            } while (duration > currentMax && !maxTime.compareAndSet(currentMax, duration));

            if (success) {
                successCount.incrementAndGet();
            }
        }

        long getCount() {
            return count.get();
        }

        double getAverageTime() {
            long c = count.get();
            return c > 0 ? (double) totalTime.get() / c : 0;
        }

        long getMaxTime() {
            return maxTime.get();
        }

        double getSuccessRate() {
            long c = count.get();
            return c > 0 ? (double) successCount.get() / c : 0;
        }
    }
}
