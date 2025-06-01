package pl.sgorski.AirLink.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.sgorski.AirLink.model.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @NonNull
    @Query("SELECT f FROM Flight f WHERE f.deletedAt IS NULL")
    List<Flight> findAll();

    @Query("SELECT f FROM Flight f LEFT JOIN FETCH f.reservations WHERE f.id = :id AND f.deletedAt IS NULL")
    Optional<Flight> findByIdWithReservations(Long id);
}
