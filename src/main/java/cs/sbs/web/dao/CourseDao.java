package cs.sbs.web.dao;

import cs.sbs.web.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseDao {
    Optional<Course> findById(Long id);

    List<Course> findAll();

    List<Course> findByTitleContaining(String keyword);

    Course save(Course course);
}
