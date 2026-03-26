package cs.sbs.web.dao;

import cs.sbs.web.entity.Course;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CourseDaoImpl implements CourseDao {
    private final ConcurrentHashMap<Long, Course> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @PostConstruct
    public void initData() {
        System.out.println("📚 CourseDaoImpl: @PostConstruct - 初始化数据");
        save(new Course(null, "Java基础入门", "面向对象、集合、异常与IO", 1200));
        save(new Course(null, "Spring AOP实战", "日志、性能监控与事务切面", 860));
        save(new Course(null, "Web开发基础", "HTTP、Servlet与MVC模式", 540));
    }

    @Override
    public Optional<Course> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Course> findByTitleContaining(String keyword) {
        if (keyword == null) {
            return findAll();
        }
        String normalized = keyword.trim().toLowerCase();
        return store.values().stream()
                .filter(course -> course.getTitle() != null && course.getTitle().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }

    @Override
    public Course save(Course course) {
        if (course.getId() == null) {
            course.setId(idGenerator.incrementAndGet());
        } else {
            idGenerator.accumulateAndGet(course.getId(), Math::max);
        }
        store.put(course.getId(), course);
        return course;
    }
}
