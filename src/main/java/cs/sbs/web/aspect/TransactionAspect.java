package cs.sbs.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class TransactionAspect {

    @Pointcut(
            "execution(* cs.sbs.web.service.*.save*(..)) || " +
            "execution(* cs.sbs.web.service.*.update*(..)) || " +
            "execution(* cs.sbs.web.service.*.delete*(..)) || " +
            "execution(* cs.sbs.web.service.*.create*(..)) || " +
            "execution(* cs.sbs.web.service.*.add*(..)) || " +
            "execution(* cs.sbs.web.service.*.increment*(..)) || " +
            "execution(* cs.sbs.web.service.*.enroll*(..))"
    )
    public void transactionalMethods() {
    }

    @Around("transactionalMethods()")
    public Object manageTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        System.out.printf("%n🔒 [事务开始] %s.%s%n", className, methodName);
        System.out.println("   开启数据库连接...");
        System.out.println("   设置自动提交为false...");

        try {
            Object result = joinPoint.proceed();

            System.out.println("   提交事务...");
            System.out.printf("✅ [事务提交] %s.%s 执行成功%n%n", className, methodName);
            return result;
        } catch (Exception e) {
            System.err.println("   回滚事务...");
            System.err.printf("❌ [事务回滚] %s.%s 执行失败: %s%n%n", className, methodName, e.getMessage());
            throw e;
        } finally {
            System.out.println("   关闭数据库连接...");
        }
    }
}
