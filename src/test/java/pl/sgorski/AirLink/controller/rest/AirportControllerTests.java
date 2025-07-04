package pl.sgorski.AirLink.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.AirLink.dto.AirportRequest;
import pl.sgorski.AirLink.dto.AirportResponse;
import pl.sgorski.AirLink.mapper.AirportMapper;
import pl.sgorski.AirLink.model.Airport;
import pl.sgorski.AirLink.service.AirportService;
import pl.sgorski.AirLink.service.auth.JwtService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AirportController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AirportControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AirportService airportService;

    @MockitoBean
    private AirportMapper airportMapper;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturnEmptyAirports() throws Exception {
        when(airportService.findAll(any(), any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(get("/api/airports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Airports"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(airportService, times(1)).findAll(any(), any(Pageable.class));
    }

    @Test
    void shouldReturnAirports() throws Exception {
        Page<Airport> airportsPage = new PageImpl<>(List.of(new Airport(), new Airport()));
        when(airportService.findAll(any(), any(Pageable.class))).thenReturn(airportsPage);
        when(airportMapper.toResponse(any(Airport.class))).thenReturn(new AirportResponse());

        mockMvc.perform(get("/api/airports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Airports"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(airportService, times(1)).findAll(any(), any(Pageable.class));
    }

    @Test
    void shouldReturnAirportById() throws Exception {
        when(airportService.findById(anyLong())).thenReturn(new Airport());
        when(airportMapper.toResponse(any(Airport.class))).thenReturn(new AirportResponse());

        mockMvc.perform(get("/api/airports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Airport"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(airportService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnProblemWhenAirportNotFound() throws Exception {
        when(airportService.findById(anyLong())).thenThrow(new NoSuchElementException("Airport not found"));

        mockMvc.perform(get("/api/airports/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Airport not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(airportService, times(1)).findById(1L);
    }

    @Test
    void shouldCreateAirport() throws Exception {
        Airport airport = new Airport();
        AirportResponse response = new AirportResponse();

        when(airportMapper.toAirport(any(AirportRequest.class))).thenReturn(airport);
        when(airportService.save(any(Airport.class))).thenReturn(airport);
        when(airportMapper.toResponse(any(Airport.class))).thenReturn(response);

        mockMvc.perform(post("/api/airports")
                        .contentType("application/json")
                        .content("{\"cityId\":1,\"icao\":\"ABCD\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detail").value("Airport created"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(airportService, times(1)).save(any(Airport.class));
    }

    @Test
    void shouldUpdateAirport() throws Exception {
        Airport airport = new Airport();
        AirportResponse response = new AirportResponse();

        when(airportService.findById(anyLong())).thenReturn(airport);
        when(airportMapper.toAirport(any(AirportRequest.class))).thenReturn(airport);
        when(airportService.save(any(Airport.class))).thenReturn(airport);
        when(airportMapper.toResponse(any(Airport.class))).thenReturn(response);

        mockMvc.perform(put("/api/airports/1")
                        .contentType("application/json")
                        .content("{\"cityId\":2,\"icao\":\"EFGH\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Airport updated"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(airportService, times(1)).findById(1L);
        verify(airportService, times(1)).save(any(Airport.class));
    }
}