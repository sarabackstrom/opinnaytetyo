package opinnaytetyo.arviointikirja.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
//cascadet pitää tehdä
@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {
 
@OneToMany (mappedBy = "teacher", cascade = CascadeType.ALL)
private List<TeachingGroup>teachingGroups = new ArrayList<>();

public Teacher() {}

public Teacher(String username, String passwordHash, String firstName, String lastName) {
    super(username, passwordHash, firstName, lastName);
}

public List<TeachingGroup> getTeachingGroups() {
    return teachingGroups;
}

public void setTeachingGroups(List<TeachingGroup> teachingGroups) {
    this.teachingGroups = teachingGroups;
}

@Override
public String toString() {
    return "Teacher [teachingGroups=" + teachingGroups + "]";
}

}
