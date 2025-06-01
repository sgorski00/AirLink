package pl.sgorski.AirLink.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sgorski.AirLink.model.Airplane;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Long> {

    @NonNull
    @Query("SELECT a FROM Airplane a WHERE a.deletedAt IS NULL")
    List<Airplane> findAll();

    @Query("SELECT a FROM Airplane a LEFT JOIN FETCH a.flights WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Airplane> findByIdWithFlights(Long id);
}
