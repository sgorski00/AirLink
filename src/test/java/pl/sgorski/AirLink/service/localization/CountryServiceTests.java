package pl.sgorski.AirLink.service.localization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sgorski.AirLink.model.localization.Country;
import pl.sgorski.AirLink.repository.localization.CountryRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CountryServiceTests {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @Test
    void shouldCountCountries() {
        when(countryRepository.count()).thenReturn(5L);

        long count = countryService.count();

        assertEquals(5L, count);
    }

    @Test
    void shouldSaveCountry() {
        Country country = new Country();
        country.setName("Test Country");
        country.setCode("TC");

        when(countryRepository.save(any(Country.class))).thenReturn(country);

        Country result = countryService.save(country);

        assertNotNull(result);
        assertEquals("Test Country", result.getName());
        assertEquals("TC", result.getCode());
    }

    @Test
    void shouldFindAllCountries() {
        Country country1 = new Country();
        country1.setName("Country 1");
        Country country2 = new Country();
        country2.setName("Country 2");

        when(countryRepository.findAll()).thenReturn(List.of(country1, country2));

        List<Country> countries = countryService.findAll();

        assertNotNull(countries);
        assertEquals(2, countries.size());
        assertEquals("Country 1", countries.get(0).getName());
        assertEquals("Country 2", countries.get(1).getName());
    }

    @Test
    void shouldCheckIfCountryExistsByNameOrCode() {
        String name = "Test Country";
        String code = "TC";

        when(countryRepository.existsByNameIgnoreCaseOrCodeIgnoreCase(anyString(), anyString())).thenReturn(true);

        boolean exists = countryService.existsByNameOrCode(name, code);

        assertTrue(exists);
    }

    @Test
    void shouldFindCountryById() {
        Long countryId = 1L;
        Country country = new Country();
        country.setId(countryId);
        country.setName("Test Country");

        when(countryRepository.findById(anyLong())).thenReturn(Optional.of(country));

        Country result = countryService.findById(countryId);

        assertNotNull(result);
        assertEquals("Test Country", result.getName());
        assertEquals(countryId, result.getId());
    }

    @Test
    void shouldThrowIfCountryByIdNotFound() {
        when(countryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> countryService.findById(1L));
    }
}
