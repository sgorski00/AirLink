package pl.sgorski.AirLink.dto.generic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class PaginationResponseDto extends ResponseDto<List<?>> {
    @Schema(example = "1", description = "Current page number, starting from 1")
    private int page;
    @Schema(example = "10", description = "Number of items per page")
    private int size;
    @Schema(example = "100", description = "Total number of elements across all pages")
    private long totalElements;
    @Schema(example = "10", description = "Total number of pages available")
    private int totalPages;

    public PaginationResponseDto(String detail, int status, Page<?> data) {
        super(detail, status, data.getContent());
        this.page = data.getNumber() + 1;
        this.size = data.getSize();
        this.totalElements = data.getTotalElements();
        this.totalPages = data.getTotalPages();
    }
}
