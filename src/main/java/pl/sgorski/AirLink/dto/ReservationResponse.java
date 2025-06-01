package pl.sgorski.AirLink.dto;

import lombok.Data;

@Data
public class ReservationResponse {
    private Long id;
    private String user;
    private FlightResponse flight;
    private Integer numberOfSeats;
    private Double totalPrice;
    private String status;
    private String createdAt;
}
