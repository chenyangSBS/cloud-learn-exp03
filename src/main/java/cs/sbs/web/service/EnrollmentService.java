package cs.sbs.web.service;

import cs.sbs.web.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentService {

    @Autowired
    private CourseService courseService;

    public void enrollCourse(Long studentId, Long courseId) {
        System.out.printf("🎓 学生选课: studentId=%d, courseId=%d%n", studentId, courseId);

        Course course = courseService.getCourseById(courseId);
        System.out.println("   查询课程: " + course.getTitle());

        courseService.incrementStudentCount(courseId);
        System.out.println("   更新学习人数");

        System.out.println("   创建选课记录...");

        System.out.println("✅ 选课成功！");
    }

    public void enrollCourseWithFailure(Long studentId, Long courseId) {
        System.out.printf("🎓 学生选课（失败测试）: studentId=%d, courseId=%d%n", studentId, courseId);

        Course course = courseService.getCourseById(courseId);
        System.out.println("   查询课程: " + course.getTitle());

        courseService.incrementStudentCount(courseId);
        System.out.println("   更新学习人数");

        throw new RuntimeException("支付服务不可用");
    }
}
