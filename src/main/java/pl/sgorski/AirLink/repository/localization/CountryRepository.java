package pl.sgorski.AirLink.repository.localization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sgorski.AirLink.model.localization.Country;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    boolean existsByNameIgnoreCaseOrCodeIgnoreCase(String name, String code);
}
