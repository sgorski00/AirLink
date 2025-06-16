package pl.sgorski.AirLink.dto.generic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ResponseDto<T> {
    @Schema(example = "Some info", description = "Detailed message about the response")
    private String detail;
    @Schema(example = "200", description = "HTTP status code of the response")
    private int status;
    @Schema(description = "Data returned in the response, can be any type. To see details, check the single item endpoint documentation.")
    private T data;
}
