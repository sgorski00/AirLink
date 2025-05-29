package pl.sgorski.AirLink.service.localization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.localization.Country;
import pl.sgorski.AirLink.repository.localization.CountryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public Country save(Country country) {
        return countryRepository.save(country);
    }

    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    public long count() {
        return countryRepository.count();
    }

    public boolean existsByNameOrCode(String name, String code) {
        return countryRepository.existsByNameIgnoreCaseOrCodeIgnoreCase(name, code);
    }
}
