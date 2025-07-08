package pl.sgorski.AirLink.dto.generic;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class SortInput {
    private String sortBy;
    private String sortDir;

    public Sort toSort() {
        try {
            return Sort.by(
                    Sort.Direction.fromString(sortDir != null ? sortDir : "asc"),
                    sortBy != null ? sortBy : "id"
            );
        } catch (Exception e) {
            return Sort.by(Sort.Direction.DESC, "id");
        }
    }
}
