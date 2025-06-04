package pl.sgorski.AirLink.model;

import jakarta.persistence.*;
import lombok.Data;
import pl.sgorski.AirLink.model.auth.User;

import java.io.Serializable;

@Entity
@Table(name = "profiles")
@Data
public class Profile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "profile")
    private User user;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private String zip;
    private String city;
    private String street;

    public void clear() {
        this.firstName = null;
        this.lastName = null;
        this.phoneNumber = null;
        this.country = null;
        this.zip = null;
        this.city = null;
        this.street = null;
    }
}
