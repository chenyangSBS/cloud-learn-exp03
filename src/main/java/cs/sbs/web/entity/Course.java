package cs.sbs.web.entity;

public class Course {
    private Long id;
    private String title;
    private String description;
    private int studentCount;

    public Course() {
    }

    public Course(Long id, String title, String description, int studentCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.studentCount = studentCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", studentCount=" + studentCount +
                '}';
    }
}
