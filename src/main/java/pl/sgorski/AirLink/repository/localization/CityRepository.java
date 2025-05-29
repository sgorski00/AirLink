package pl.sgorski.AirLink.repository.localization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sgorski.AirLink.model.localization.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    boolean existsByName(String name);
}
