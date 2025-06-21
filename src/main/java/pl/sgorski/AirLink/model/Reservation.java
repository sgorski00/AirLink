package pl.sgorski.AirLink.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.thymeleaf.context.Context;
import pl.sgorski.AirLink.exception.IllegalStatusException;
import pl.sgorski.AirLink.model.auth.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static pl.sgorski.AirLink.model.ReservationStatus.*;

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

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    private List<ReservationHistory> history = new ArrayList<>();

    @PrePersist
    public void addHistory() {
        this.history.add(toHistory());
    }

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

    public ReservationHistory toHistory() {
        return ReservationHistory.builder()
                .reservation(this)
                .status(status)
                .build();
    }

    public void restore() {
        if(this.status != DELETED) return;
        this.setDeletedAt(null);
        this.status = PENDING;
        this.history.add(toHistory());
    }

    public void setStatus(ReservationStatus status) {
        if (status == null) throw new IllegalArgumentException("Reservation status cannot be null");
        if (this.status == status) return;
        if (status == PENDING) throw new IllegalStatusException("Cannot change reservation status to pending.");
        if (status == DELETED) this.setDeletedAt(Timestamp.from(Instant.now()));
        if (status == CONFIRMED && this.status != PENDING)
            throw new IllegalStatusException("Cannot confirm a reservation that is not pending.");
        if (status == CANCELLED && (this.status != PENDING && this.status != CONFIRMED))
            throw new IllegalStatusException("Cannot confirm a reservation that is not pending.");
        if (status == COMPLETED && this.status != CONFIRMED)
            throw new IllegalStatusException("Cannot complete a reservation that is not confirmed.");
        this.status = status;
        this.history.add(toHistory());
    }
}
