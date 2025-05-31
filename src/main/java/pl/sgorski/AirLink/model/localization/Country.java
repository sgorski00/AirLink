package pl.sgorski.AirLink.model.localization;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "countries")
@Data
public class Country implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @OneToMany(mappedBy = "country", fetch = FetchType.LAZY)
    private List<City> cities;
}
