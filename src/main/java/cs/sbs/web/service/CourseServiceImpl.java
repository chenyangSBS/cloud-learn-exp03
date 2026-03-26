package cs.sbs.web.service;

import cs.sbs.web.dao.CourseDao;
import cs.sbs.web.entity.Course;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseDao courseDao;

    @PostConstruct
    public void init() {
        System.out.println("🔧 CourseServiceImpl: @PostConstruct - 服务初始化完成");
    }

    @Override
    public Course getCourseById(Long id) {
        return courseDao.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在: id=" + id));
    }

    @Override
    public List<Course> getAllCourses() {
        return courseDao.findAll();
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCourses();
        }
        return courseDao.findByTitleContaining(keyword);
    }

    @Override
    public Course addCourse(Course course) {
        return courseDao.save(course);
    }

    @Override
    public void incrementStudentCount(Long id) {
        Course course = getCourseById(id);
        course.setStudentCount(course.getStudentCount() + 1);
        courseDao.save(course);
    }
}
