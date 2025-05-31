package pl.sgorski.AirLink.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class FlightResponse {
    private Long id;
    private String from;
    private String to;
    private LocalDateTime departure;
    private LocalDateTime arrival;
    private String price;
    private String airplaneName;
    private Timestamp createdAt;
}
