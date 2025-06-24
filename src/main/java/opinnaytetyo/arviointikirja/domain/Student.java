package opinnaytetyo.arviointikirja.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @ManyToOne
    @JoinColumn(name = "opetusryhma")
    //@NotNull(message = "Oppilaalla pitää olla opetusryhmä")
    private TeachingGroup teachingGroup;

    @OneToMany (mappedBy = "student", cascade = CascadeType.ALL)
    private List<Performance> performances = new ArrayList<>();

    public Student(){}

    public Student(String username, String passwordHash, String firstName, String lastName, TeachingGroup teachingGroup) {
        super(username, passwordHash, firstName, lastName);
        this.teachingGroup = teachingGroup;
    }

    public TeachingGroup getTeachingGroup() {
        return teachingGroup;
    }

    public void setTeachingGroup(TeachingGroup teachingGroup) {
        this.teachingGroup = teachingGroup;
    }

    public List<Performance> getPerformances() {
        return performances;
    }

    public void setPerformances(List<Performance> performances) {
        this.performances = performances;
    }

    @Override
    public String toString() {
        return "Student [teachingGroup=" + teachingGroup + "]";
    }
}
