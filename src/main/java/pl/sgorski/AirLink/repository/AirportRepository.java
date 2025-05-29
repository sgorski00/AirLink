package pl.sgorski.AirLink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sgorski.AirLink.model.Airport;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
}
