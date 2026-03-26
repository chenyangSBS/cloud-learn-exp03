package cs.sbs.web;

import cs.sbs.web.aspect.PerformanceAspect;
import cs.sbs.web.config.AppConfig;
import cs.sbs.web.entity.Course;
import cs.sbs.web.service.CourseService;
import cs.sbs.web.service.EnrollmentService;
import cs.sbs.web.service.PerformanceTestService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.lang.reflect.Proxy;
import org.aspectj.lang.annotation.Aspect;

public class Main {

    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0].trim().toLowerCase() : "all";

        System.out.println("🚀 启动智学云平台（Spring AOP演示）\n");
        System.out.println("可选模式: baseline | log | perf | tx | all");
        System.out.println("当前模式: " + mode + "\n");

        switch (mode) {
            case "baseline" -> runBaselineDemo();
            case "log" -> runWithAop(Main::runLoggingDemo);
            case "perf" -> runWithAop(Main::runPerformanceDemo);
            case "tx" -> runWithAop(Main::runTransactionDemo);
            case "all" -> runWithAop(context -> {
                runLoggingDemo(context);
                runPerformanceDemo(context);
                runTransactionDemo(context);
            });
            default -> {
                System.out.println("未知模式: " + mode);
                System.out.println("用法: mvn -q -DskipTests compile exec:java -Dexec.args=\"all\"");
            }
        }
    }

    private static void runBaselineDemo() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(BaselineConfig.class);
        context.refresh();
        try {
            CourseService courseService = context.getBean(CourseService.class);
            System.out.println("========== 基线阶段：无 AOP ==========");
            System.out.println("CourseService Bean类型: " + courseService.getClass().getName());
            System.out.println("是否JDK动态代理: " + Proxy.isProxyClass(courseService.getClass()));

            System.out.println("\n=== 测试1: 查询所有课程 ===");
            courseService.getAllCourses();

            System.out.println("\n=== 测试2: 根据ID查询 ===");
            Course course = courseService.getCourseById(1L);
            System.out.println("课程: " + course);

            System.out.println("\n=== 测试3: 搜索课程 ===");
            courseService.searchCourses("Java");

            System.out.println("\n=== 测试4: 异常测试 ===");
            try {
                courseService.getCourseById(999L);
            } catch (Exception e) {
                System.err.println("异常: " + e.getMessage());
            }
            System.out.println();
        } finally {
            context.close();
        }
    }

    private static void runWithAop(java.util.function.Consumer<AnnotationConfigApplicationContext> runner) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        try {
            runner.accept(context);
        } finally {
            context.close();
        }
    }

    private static void runLoggingDemo(AnnotationConfigApplicationContext context) {
        CourseService courseService = context.getBean(CourseService.class);

        System.out.println("========== 第一部分：日志切面 ==========");
        System.out.println("CourseService Bean类型: " + courseService.getClass().getName());
        System.out.println("是否JDK动态代理: " + Proxy.isProxyClass(courseService.getClass()));
        System.out.println("=== 测试1: 查询所有课程 ===");
        courseService.getAllCourses();

        System.out.println("\n=== 测试2: 根据ID查询 ===");
        Course course = courseService.getCourseById(1L);
        System.out.println("课程: " + course);

        System.out.println("\n=== 测试3: 搜索课程 ===");
        courseService.searchCourses("Java");

        System.out.println("\n=== 测试4: 异常测试 ===");
        try {
            courseService.getCourseById(999L);
        } catch (Exception ignored) {
        }
        System.out.println();
    }

    private static void runPerformanceDemo(AnnotationConfigApplicationContext context) {
        PerformanceTestService testService = context.getBean(PerformanceTestService.class);

        System.out.println("========== 第二部分：性能监控切面 ==========");
        System.out.println("=== 性能测试开始 ===\n");

        testService.fastOperation();
        testService.normalOperation();
        testService.slowOperation();
        testService.batchOperation(5);

        for (int i = 0; i < 3; i++) {
            testService.fastOperation();
        }

        PerformanceAspect aspect = context.getBean(PerformanceAspect.class);
        aspect.printStatsReport();
    }

    private static void runTransactionDemo(AnnotationConfigApplicationContext context) {
        EnrollmentService enrollmentService = context.getBean(EnrollmentService.class);

        System.out.println("========== 第三部分：事务模拟切面 ==========");

        System.out.println("=== 测试1: 正常选课 ===");
        try {
            enrollmentService.enrollCourse(1L, 1L);
        } catch (Exception e) {
            System.err.println("异常: " + e.getMessage());
        }

        System.out.println("\n=== 测试2: 选课（模拟失败）===");
        try {
            enrollmentService.enrollCourseWithFailure(2L, 2L);
        } catch (Exception e) {
            System.err.println("异常: " + e.getMessage());
        }
        System.out.println();
    }

    @Configuration
    @ComponentScan(
            basePackages = {"cs.sbs.web.dao", "cs.sbs.web.service"},
            excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Aspect.class)
    )
    static class BaselineConfig {
    }
}
