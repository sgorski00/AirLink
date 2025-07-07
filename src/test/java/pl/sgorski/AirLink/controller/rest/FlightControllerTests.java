package pl.sgorski.AirLink.controller.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.AirLink.dto.FlightResponse;
import pl.sgorski.AirLink.mapper.FlightMapper;
import pl.sgorski.AirLink.model.Flight;
import pl.sgorski.AirLink.service.FlightService;
import pl.sgorski.AirLink.service.auth.JwtService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FlightControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlightService flightService;

    @MockitoBean
    private FlightMapper flightMapper;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturnEmptyFlights() throws Exception {
        when(flightService.findAllActivePaginated(any(Pageable.class), any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Flights"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(flightService, times(1)).findAllActivePaginated(any(Pageable.class), any(), any());
    }

    @Test
    void shouldReturnFlights() throws Exception {
        Page<Flight> flightsPage = new PageImpl<>(List.of(new Flight(), new Flight()));
        when(flightService.findAllActivePaginated(any(Pageable.class), any(), any())).thenReturn(flightsPage);
        when(flightMapper.toResponse(any(Flight.class))).thenReturn(new FlightResponse());

        mockMvc.perform(get("/api/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Flights"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(flightService, times(1)).findAllActivePaginated(any(Pageable.class), any(), any());
    }

    @Test
    void shouldThrow_WrongSortItem() throws Exception {
        Page<Flight> flightsPage = new PageImpl<>(List.of(new Flight(), new Flight()));
        when(flightService.findAllActivePaginated(any(Pageable.class), any(), any())).thenReturn(flightsPage);
        when(flightMapper.toResponse(any(Flight.class))).thenReturn(new FlightResponse());

        mockMvc.perform(get("/api/flights")
                        .param("sortBy", "wrongField")
                        .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Flights"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(flightService, times(1)).findAllActivePaginated(any(Pageable.class), any(), any());
    }

    @Test
    void shouldThrow_WrongSortDirection() throws Exception {
        Page<Flight> flightsPage = new PageImpl<>(List.of(new Flight(), new Flight()));
        when(flightService.findAllActivePaginated(any(Pageable.class), anyLong(), anyLong())).thenReturn(flightsPage);
        when(flightMapper.toResponse(any(Flight.class))).thenReturn(new FlightResponse());

        mockMvc.perform(get("/api/flights")
                        .param("sortBy", "id")
                        .param("sortDir", "aa"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").isString())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(flightService, never()).findAllActivePaginated(any(Pageable.class), anyLong(), anyLong());
    }

    @Test
    void shouldReturnFlightById() throws Exception {
        when(flightService.findById(anyLong())).thenReturn(new Flight());
        when(flightMapper.toResponse(any(Flight.class))).thenReturn(new FlightResponse());

        mockMvc.perform(get("/api/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Flight found"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(flightService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnProblemWhenFlightNotFound() throws Exception {
        when(flightService.findById(anyLong())).thenThrow(new NoSuchElementException("Flight not found"));

        mockMvc.perform(get("/api/flights/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Flight not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(flightService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnProblemWhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/flights/one"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(flightService, never()).findById(anyLong());
    }

    @Test
    void shouldSoftDeleteFlight() throws Exception {
        Flight flight = new Flight();
        when(flightService.deleteById(anyLong())).thenReturn(flight);
        when(flightMapper.toResponse(any(Flight.class))).thenReturn(new FlightResponse());

        mockMvc.perform(delete("/api/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Flight deleted"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(flightService, times(1)).deleteById(1L);
    }

    @Test
    void shouldRestoreFlight() throws Exception {
        Flight flight = new Flight();
        when(flightService.restoreById(anyLong())).thenReturn(flight);
        when(flightMapper.toResponse(any(Flight.class))).thenReturn(new FlightResponse());

        mockMvc.perform(put("/api/flights/restore/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Flight restored"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(flightService, times(1)).restoreById(1L);
    }

    //TODO: Add tests for creating and updating flights
}
