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
    @Override
    List<Flight> findAll();

    @NonNull
    @Query("SELECT f FROM Flight f WHERE f.deletedAt IS NULL AND f.id = :id")
    @Override
    Optional<Flight> findById(@NonNull Long id);

    @Query("SELECT f FROM Flight f LEFT JOIN FETCH f.reservations WHERE f.id = :id AND f.deletedAt IS NULL")
    Optional<Flight> findByIdWithReservations(Long id);

    @Query("SELECT f FROM Flight f WHERE f.deletedAt IS NOT NULL AND f.id = :id")
    Optional<Flight> findDeletedById(Long id);
}
