package pl.sgorski.AirLink.dto.generic;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PageInputTests {

    @Test
    void shouldCreatePageRequest() {
        PageInput pageInput = new PageInput();
        pageInput.setPage(2);
        pageInput.setSize(20);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        PageRequest result = pageInput.toPageRequest(sort);

        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        assertEquals(20, result.getPageSize());
    }

    @Test
    void shouldCreatePageRequest_NullSort() {
        PageInput pageInput = new PageInput();
        pageInput.setPage(2);
        pageInput.setSize(20);

        PageRequest result = pageInput.toPageRequest(null);

        assertNotNull(result);
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
    }

    @Test
    void shouldCreatePageRequest_NotPositiveSize() {
        PageInput pageInput = new PageInput();
        pageInput.setPage(2);
        pageInput.setSize(-20);
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        PageRequest result = pageInput.toPageRequest(sort);

        assertNotNull(result);
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
    }
}
