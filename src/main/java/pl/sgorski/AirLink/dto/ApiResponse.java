package pl.sgorski.AirLink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private String detail;
    private int status;
    private T data;
}
