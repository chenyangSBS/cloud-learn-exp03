package cs.sbs.web.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* cs.sbs.web.service.*.*(..))")
    public void serviceLayer() {
    }

    @Before("serviceLayer()")
    public void beforeAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        System.out.printf("%n📝 [%s] 前置通知%n", timestamp);
        System.out.printf("   调用: %s.%s()%n", className, methodName);
        if (args.length > 0) {
            System.out.printf("   参数: %s%n", Arrays.toString(args));
        }
    }

    @After("serviceLayer()")
    public void afterAdvice(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.printf("📝 后置通知: %s 执行完成%n%n", methodName);
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void afterReturningAdvice(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        System.out.printf("✅ 返回通知: %s 返回结果: %s%n", methodName, result);
    }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "ex")
    public void afterThrowingAdvice(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        System.err.printf("❌ 异常通知: %s 抛出异常: %s%n", methodName, ex.getMessage());
    }
}
