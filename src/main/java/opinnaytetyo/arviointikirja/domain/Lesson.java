package opinnaytetyo.arviointikirja.domain;

import java.time.LocalDate;
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
import jakarta.validation.constraints.NotNull;

@Entity
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name="paivamaara", nullable = false)
    //@NotEmpty(message = "Oppitunnilla pitää olla päivämäärä")
    private LocalDate lessonDay;

    @Column(name="kuvaus")
    private String lessonDescription;

    @ManyToOne (optional = false)
    @JoinColumn (name ="teachingGroupId", nullable = false)
    @NotNull(message = "Oppituntiin pitää liittyä opetusryhmä.")
    private TeachingGroup teachingGroup;

    @ManyToOne (optional = false)
    @JoinColumn(name= "sportId", nullable = false)
    @NotNull(message = "Oppitunnilla pitää olla liikuntalaji.")
    private Sport sport;

    @OneToMany (mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Performance> performances = new ArrayList<>();

    @OneToMany (mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<LessonGoal> lessonGoals = new ArrayList<>();

    public Lesson(){}

    public Lesson(LocalDate lessonDay, String lessonDescription, TeachingGroup teachingGroup, Sport sport) {
        this.lessonDay = lessonDay;
        this.lessonDescription = lessonDescription;
        this.teachingGroup = teachingGroup;
        this.sport = sport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getLessonDay() {
        return lessonDay;
    }

    public void setLessonDay(LocalDate lessonDay) {
        this.lessonDay = lessonDay;
    }

    public String getLessonDescription() {
        return lessonDescription;
    }

    public void setLessonDescription(String lessonDescription) {
        this.lessonDescription = lessonDescription;
    }

    public TeachingGroup getTeachingGroup() {
        return teachingGroup;
    }

    public void setTeachingGroup(TeachingGroup teachingGroup) {
        this.teachingGroup = teachingGroup;
    }

    public Sport getSport() {
        return sport;
    }

    public void setSport(Sport sport) {
        this.sport = sport;
    }

    public List<Performance> getPerformances() {
        return performances;
    }

    public void setPerformances(List<Performance> performances) {
        this.performances = performances;
    }

    public List<LessonGoal> getLessonGoals() {
        return lessonGoals;
    }

    public void setLessonGoals(List<LessonGoal> lessonGoals) {
        this.lessonGoals = lessonGoals;
    }

    @Override
    public String toString() {
        return "Lesson [id=" + id + ", lessonDay=" + lessonDay + ", lessonDescription=" + lessonDescription
                + ", teachingGroup=" + teachingGroup + ", sport=" + sport + "]";
    }

}
