package pl.sgorski.AirLink.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "reservations_history")
@ToString(exclude = "reservation")
@EqualsAndHashCode(exclude = "reservation")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservationHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @CreationTimestamp
    private Timestamp createdAt;

}
