package pl.sgorski.AirLink.dto.generic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ResponseDto<T> {
    private String detail;
    private int status;
    private T data;
}
