package pl.sgorski.AirLink.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.model.localization.Country;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {
    @Query("SELECT a FROM Airport a JOIN a.city c WHERE c.country = :country ORDER BY a.city.name")
    Page<Airport> findAllByCountry(Country country, Pageable pageable);

    @Query("SELECT a FROM Airport a ORDER BY a.city.name")
    Page<Airport> findAll(Long countryId, Pageable pageable);
}
