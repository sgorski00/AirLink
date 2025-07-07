package pl.sgorski.AirLink.dto.generic;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;

public class SortInputTests {

    @Test
    void shouldConvertToSort_CorrectData() {
        SortInput sortInput = new SortInput();
        sortInput.setSortBy("name");
        sortInput.setSortDir("desc");

        Sort sort = sortInput.toSort();

        assertNotNull(sort);
        assertNotNull(sort.getOrderFor("name"));
        assertTrue(sort.getOrderFor("name").isDescending());
    }

    @Test
    void shouldConvertToSort_NullSortBy() {
        SortInput sortInput = new SortInput();
        sortInput.setSortDir("asc");

        Sort sort = sortInput.toSort();

        assertNotNull(sort);
        assertNotNull(sort.getOrderFor("id"));
        assertTrue(sort.getOrderFor("id").isAscending());
    }

    @Test
    void shouldConvertToSort_NullSortDir() {
        SortInput sortInput = new SortInput();
        sortInput.setSortBy("date");

        Sort sort = sortInput.toSort();

        assertNotNull(sort);
        assertNotNull(sort.getOrderFor("date"));
        assertTrue(sort.getOrderFor("date").isAscending());
    }

    @Test
    void shouldConvertToSort_WrongSortDir() {
        SortInput sortInput = new SortInput();
        sortInput.setSortBy("date");
        sortInput.setSortDir("wrong");

        Sort sort = sortInput.toSort();

        assertNotNull(sort);
        assertNotNull(sort.getOrderFor("id"));
        assertTrue(sort.getOrderFor("id").isDescending());
    }
}
