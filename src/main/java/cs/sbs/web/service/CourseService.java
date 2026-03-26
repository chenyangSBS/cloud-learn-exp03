package cs.sbs.web.service;

import cs.sbs.web.entity.Course;

import java.util.List;

public interface CourseService {
    Course getCourseById(Long id);

    List<Course> getAllCourses();

    List<Course> searchCourses(String keyword);

    Course addCourse(Course course);

    void incrementStudentCount(Long id);
}
