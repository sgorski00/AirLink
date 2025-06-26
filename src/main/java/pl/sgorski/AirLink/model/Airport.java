package pl.sgorski.AirLink.model;

import jakarta.persistence.*;
import lombok.Data;
import pl.sgorski.AirLink.dto.AirportRequest;
import pl.sgorski.AirLink.model.localization.City;

import java.io.Serializable;

@Entity
@Table(name = "airports")
@Data
public class Airport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;

    @Column(nullable = false, unique = true)
    private String icao;

    public void update(Airport airport) {
        this.city = airport.getCity();
        this.icao = airport.getIcao();
    }
}
