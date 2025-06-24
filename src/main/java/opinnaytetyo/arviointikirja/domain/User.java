package opinnaytetyo.arviointikirja.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;

//tietokantaan ja sovelluslogiikkaan tarkistukset @Column sekä @NotEmpty (nolla-arvot), uniikit arvot, @column nimen määrittelyt tietokantaan https://medium.com/@erayaraz10/understanding-discriminatorcolumn-in-spring-jpa-ae61bbc6ca68
@Entity
@Table(name="Kayttajat")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "rooli", discriminatorType = DiscriminatorType.STRING)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "kayttajanimi", nullable = false, unique = true)
    @NotEmpty(message = "Käyttäjänimi ei voi olla tyhjä.")
    private String username;

    @Column(name = "salasana", nullable = false)
    @NotEmpty(message = "Salasana ei voi olla tyhjä.")
    private String passwordHash;

    @Column(name = "etunimi", nullable = false)
    @NotEmpty(message = "Etunimi ei voi olla tyhjä.")
    private String firstName;

    @Column(name = "sukunimi", nullable = false)
    @NotEmpty(message = "Sukunimi ei voi olla tyhjä.")
    private String lastName;

   @OneToMany (mappedBy = "user", cascade = CascadeType.ALL)
   private List<Performance> performances = new ArrayList<>();

   public User(){}

   public User(String username, String passwordHash, String firstName, String lastName) {
    this.username = username;
    this.passwordHash = passwordHash;
    this.firstName = firstName;
    this.lastName = lastName;
   }

   public Long getId() {
    return id;
   }

   public void setId(Long id) {
    this.id = id;
   }

   public String getUsername() {
    return username;
   }

   public void setUsername(String username) {
    this.username = username;
   }

   public String getPasswordHash() {
    return passwordHash;
   }

   public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
   }

   public String getFirstName() {
    return firstName;
   }

   public void setFirstName(String firstName) {
    this.firstName = firstName;
   }

   public String getLastName() {
    return lastName;
   }

   public void setLastName(String lastName) {
    this.lastName = lastName;
   }

   public List<Performance> getPerformances() {
    return performances;
   }

   public void setPerformances(List<Performance> performances) {
    this.performances = performances;
   }

   @Override
   public String toString() {
    return "User [id=" + id + ", username=" + username + ", firstName=" + firstName
            + ", lastName=" + lastName + "]";
   }

}
