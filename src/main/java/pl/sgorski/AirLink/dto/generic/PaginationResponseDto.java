package pl.sgorski.AirLink.dto.generic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PaginationResponseDto extends ResponseDto<List<?>> {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PaginationResponseDto(String detail, int status, Page<?> data) {
        super(detail, status, data.getContent());
        this.page = data.getNumber() + 1;
        this.size = data.getSize();
        this.totalElements = data.getTotalElements();
        this.totalPages = data.getTotalPages();
    }
}
