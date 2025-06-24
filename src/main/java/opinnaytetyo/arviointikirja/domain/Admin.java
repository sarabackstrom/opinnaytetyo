package opinnaytetyo.arviointikirja.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

//pitääkö olla oma konstruktori?
@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    public Admin(){}

    public Admin(String username, String passwordHash, String firstName, String lastName) {
    super(username, passwordHash, firstName, lastName);
}

}
