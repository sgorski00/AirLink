package pl.sgorski.AirLink.dto.generic;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
public class PageInput {
    private int page = 1;
    private int size = 10;

    public PageRequest toPageRequest(Sort sort) {
        try {
            return PageRequest.of(page - 1, size, sort);
        }catch (Exception e) {
            return PageRequest.of(0, 10);
        }
    }
}
