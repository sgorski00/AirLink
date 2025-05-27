package pl.sgorski.AirLink.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private String message;
    private int code;
    private T data;
}
