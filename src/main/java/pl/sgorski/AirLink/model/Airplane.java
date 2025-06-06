package pl.sgorski.AirLink.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "airplanes")
@Data
public class Airplane implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private int seats;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private Timestamp deletedAt;

    @OneToMany(mappedBy = "airplane", fetch = FetchType.LAZY)
    private List<Flight> flights;

    public boolean isAvailable(LocalDateTime departure, LocalDateTime arrival) {
        if(flights == null || flights.isEmpty()) return true;
        return flights.stream()
                .filter(flight -> flight.getDeletedAt() == null)
                .noneMatch(flight ->
                    (departure.isBefore(flight.getArrival()) && arrival.isAfter(flight.getDeparture()))
                );
    }
}
