package pl.sgorski.AirLink.service.localization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.AirLink.model.localization.City;
import pl.sgorski.AirLink.repository.localization.CityRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CityServiceTests {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    @Test
    void shouldCountCities() {
        when(cityRepository.count()).thenReturn(10L);

        long result = cityService.count();

        assertEquals(10L, result);
    }

    @Test
    void shouldSaveCity() {
        City city = new City();
        city.setName("Test City");

        when(cityRepository.save(any(City.class))).thenReturn(city);

        City savedCity = cityService.save(city);

        assertNotNull(savedCity);
        assertEquals("Test City", savedCity.getName());
        verify(cityRepository, times(1)).save(city);
    }

    @Test
    void shouldFindAllCities() {
        City city1 = new City();
        city1.setName("City 1");
        City city2 = new City();
        city2.setName("City 2");

        when(cityRepository.findAll()).thenReturn(List.of(city1, city2));

        List<City> cities = cityService.findAll();

        assertNotNull(cities);
        assertEquals(2, cities.size());
        assertEquals("City 1", cities.get(0).getName());
        assertEquals("City 2", cities.get(1).getName());
    }

    @Test
    void shouldCheckIfCityExistsByName() {
        String cityName = "Existing City";
        when(cityRepository.existsByName(anyString())).thenReturn(true);

        boolean result = cityService.existsByName(cityName);

        assertTrue(result);
        verify(cityRepository, times(1)).existsByName(anyString());
    }

    @Test
    void shouldFindCityById() {
        City city = new City();
        city.setId(1L);
        city.setName("City by ID");

        when(cityRepository.findById(anyLong())).thenReturn(java.util.Optional.of(city));

        City foundCity = cityService.findById(1L);

        assertNotNull(foundCity);
        assertEquals(1L, foundCity.getId());
        assertEquals("City by ID", foundCity.getName());
    }

    @Test
    void shouldThrowIfCityByIdNotFound() {
        when(cityRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> cityService.findById(1L));
    }
}
