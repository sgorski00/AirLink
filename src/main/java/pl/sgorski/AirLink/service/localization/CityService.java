package pl.sgorski.AirLink.service.localization;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.localization.City;
import pl.sgorski.AirLink.repository.localization.CityRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public long count() {
        return cityRepository.count();
    }

    public City save(City city) {
        return cityRepository.save(city);
    }

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public boolean existsByName(String name) {
        return cityRepository.existsByName(name);
    }

    public City findById(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("City with id " + id + " not found"));
    }
}
