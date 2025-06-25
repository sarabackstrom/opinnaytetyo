package opinnaytetyo.arviointikirja.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "liikuntataidot")
    @Min(4)
    @Max(10)
    private int skills;

    @Column(name = "tyoskentely")
   @Min(4)
    @Max(10)
    private int effort;

    @Column(name = "kuvaus")
    private String shortDescription;

    @Column(name = "poissaolo")
    private boolean absence;

    @Column(name = "ei_varusteita")
    private boolean sportsEquipment;

    @Column(name = "ei_osallistu")
    private boolean participation;

    @Column(name = "loukkaantunut")
    private boolean injured;

    @ManyToOne (optional = false)
    @JoinColumn(name = "studentId", nullable = false)
    @NotNull(message = "Suorituksessa pitää määritellä oppilas.")
    private Student student;

    @ManyToOne (optional = false)
    @JoinColumn(name = "addedById", nullable = false)
    @NotNull(message = "Suorituksessa pitää olla tieto lisääjästä.")
    private User user;

    @ManyToOne (optional = false)
    @JoinColumn(name = "lessonId", nullable = false)
    @NotNull(message = "Suorituksessa pitää määritellä oppitunti.")
    private Lesson lesson;

    public Performance(){}

    public Performance(int skills, int effort, String shortDescription, boolean absence, boolean sportsEquipment, boolean participation, boolean injured, Student student, User user, Lesson lesson) {
        this.skills = skills;
        this.effort = effort;
        this.shortDescription = shortDescription;
        this.absence = absence;
        this.sportsEquipment = sportsEquipment;
        this.participation = participation;
        this.injured = injured;
        this.student = student;
        this.user = user;
        this.lesson = lesson;
    }

    /*  lisätty jälkikäteen, voiko poistaa?
    public Performance(int skills, int effort, String shortDescription, Student student, User user, Lesson lesson) {
    this(skills, effort, shortDescription, false, false, false, false, student, user, lesson);
}*/

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSkills() {
        return skills;
    }

    public void setSkills(int skills) {
        this.skills = skills;
    }

    public int getEffort() {
        return effort;
    }

    public void setEffort(int effort) {
        this.effort = effort;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public boolean isAbsence() {
        return absence;
    }

    public void setAbsence(boolean absence) {
        this.absence = absence;
    }

    public boolean isSportsEquipment() {
        return sportsEquipment;
    }

    public void setSportsEquipment(boolean sportsEquipment) {
        this.sportsEquipment = sportsEquipment;
    }

    public boolean isParticipation() {
        return participation;
    }

    public void setParticipation(boolean participation) {
        this.participation = participation;
    }

    public boolean isInjured() {
        return injured;
    }

    public void setInjured(boolean injured) {
        this.injured = injured;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    @Override
    public String toString() {
        return "Performance [id=" + id + ", skills=" + skills + ", effort=" + effort + ", shortDescription="
                + shortDescription + ", absence=" + absence + ", sportsEquipment=" + sportsEquipment
                + ", participation=" + participation + ", injured=" + injured + ", student=" + student + ", user="
                + user + ", lesson=" + lesson + "]";
    }    

}
