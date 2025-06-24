package opinnaytetyo.arviointikirja.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class LessonGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne (optional = false)
    @NotNull
    private Lesson lesson;

    @ManyToOne (optional = false)
    @NotNull
    private EducationalGoal educationalGoal;

    public LessonGoal(){}

    public LessonGoal(Lesson lesson, EducationalGoal educationalGoal) {
        this.lesson = lesson;
        this.educationalGoal = educationalGoal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public EducationalGoal getEducationalGoal() {
        return educationalGoal;
    }

    public void setEducationalGoal(EducationalGoal educationalGoal) {
        this.educationalGoal = educationalGoal;
    }

    @Override
    public String toString() {
        return "LessonGoal [id=" + id + ", lesson=" + lesson + ", educationalGoal=" + educationalGoal + "]";
    }

}
