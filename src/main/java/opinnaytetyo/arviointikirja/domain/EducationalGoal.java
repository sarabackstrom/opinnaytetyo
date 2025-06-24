package opinnaytetyo.arviointikirja.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class EducationalGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "nimi", nullable = false, unique = true)
    private String name;

    @Column(name= "kategoria", nullable = false)
    private String category;

    @OneToMany (mappedBy = "educationalGoal", cascade = CascadeType.ALL)
    private List<LessonGoal>lessonGoals = new ArrayList<>();

    public EducationalGoal(){}

    public EducationalGoal(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<LessonGoal> getLessonGoals() {
        return lessonGoals;
    }

    public void setLessonGoals(List<LessonGoal> lessonGoals) {
        this.lessonGoals = lessonGoals;
    }

    @Override
    public String toString() {
        return "EducationalGoal [id=" + id + ", name=" + name + ", category=" + category + "]";
    }
}
