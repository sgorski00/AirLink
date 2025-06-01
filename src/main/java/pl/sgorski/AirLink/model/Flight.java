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
@Table(name = "flights")
@Data
public class Flight implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "airplane_id", nullable = false)
    private Airplane airplane;

    @Column(nullable = false)
    private LocalDateTime departure;

    @Column(nullable = false)
    private LocalDateTime arrival;

    @ManyToOne
    @JoinColumn(name = "airport_from_id", nullable = false)
    private Airport from;

    @ManyToOne
    @JoinColumn(name = "airport_to_id", nullable = false)
    private Airport to;

    @Column(nullable = false)
    private Double price;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private Timestamp deletedAt;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations;

    public long getAvailableSeats() {
        if(reservations == null) return airplane.getSeats();
        return airplane.getSeats() - reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.CONFIRMED || reservation.getStatus() == ReservationStatus.PENDING)
                .mapToInt(Reservation::getNumberOfSeats)
                .sum();
    }

    public boolean hasAvailableSeats(int numberOfSeats) {
        return getAvailableSeats() >= numberOfSeats;
    }
}
