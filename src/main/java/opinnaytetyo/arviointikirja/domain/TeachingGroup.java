package opinnaytetyo.arviointikirja.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Entity
public class TeachingGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name= "aloitusvuosi", nullable = false)
    @Min(2022)
    @Max(2050)
    private int startYear;

    @Column(name= "luokkatunnus", nullable = false)
    @NotEmpty(message = "Luokkatunnus ei voi olla tyhjä")
    private String classCode;

    @Column(name= "opetusryhmä", nullable = false)
    @NotEmpty(message = "Opetusryhmä ei voi olla tyhjä.")
    private String className;

    @ManyToOne
    @JoinColumn(name = "teacherId")
    private Teacher teacher;

    @OneToMany (mappedBy = "teachingGroup", cascade = CascadeType.ALL)
    private List<Student> students = new ArrayList<>();

    @OneToMany (mappedBy = "teachingGroup", cascade = CascadeType.ALL)
    private List<Lesson>lessons = new ArrayList<>();

    public TeachingGroup(){}

    public TeachingGroup(int startYear, String classCode, String className, Teacher teacher) {
        this.startYear = startYear;
        this.classCode = classCode;
        this.className = className;
        this.teacher = teacher;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Override
    public String toString() {
        return "TeachingGroup [id=" + id + ", startYear=" + startYear + ", classCode=" + classCode + ", className="
                + className + ", teacher=" + teacher + "]";
    }

}
