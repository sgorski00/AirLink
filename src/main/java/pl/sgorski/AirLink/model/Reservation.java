package pl.sgorski.AirLink.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.thymeleaf.context.Context;
import pl.sgorski.AirLink.model.auth.User;

import java.sql.Timestamp;

@Entity
@Table(name = "reservations")
@Data
public class Reservation implements Ownable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    private Integer numberOfSeats;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.PENDING;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private Timestamp deletedAt;

    @Override
    public Long getOwnerId() {
        return user.getId();
    }

    public double getPrice() {
        return flight.getPrice() * numberOfSeats;
    }

    public Context toEmailContext() {
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        context.setVariable("flight", flight);
        context.setVariable("seats", numberOfSeats);
        context.setVariable("price", getPrice());
        context.setVariable("status", status);
        return context;
    }
}
