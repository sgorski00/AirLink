package pl.sgorski.AirLink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.sgorski.AirLink.dto.AirplaneRequest;
import pl.sgorski.AirLink.dto.AirplaneResponse;
import pl.sgorski.AirLink.mapper.AirplaneMapper;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.service.AirplaneService;
import pl.sgorski.AirLink.service.auth.JwtService;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AirplaneController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AirplaneControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AirplaneService airplaneService;
    @MockitoBean
    private AirplaneMapper airplaneMapper;
    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldReturnPaginatedAirplanes() throws Exception {
        Page<Airplane> airplanes = new PageImpl<>(List.of(new Airplane()));
        when(airplaneService.findAll(any(PageRequest.class))).thenReturn(airplanes);
        when(airplaneMapper.toResponse(any(Airplane.class))).thenReturn(new AirplaneResponse());

        mockMvc.perform(get("/api/airplanes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Airplanes"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray());

        verify(airplaneService, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void shouldReturnAirplaneById() throws Exception {
        Airplane airplane = new Airplane();
        AirplaneResponse response = new AirplaneResponse();
        when(airplaneService.findById(anyLong())).thenReturn(airplane);
        when(airplaneMapper.toResponse(any(Airplane.class))).thenReturn(response);

        mockMvc.perform(get("/api/airplanes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Airplane"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(airplaneService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundIfAirplaneNotExists() throws Exception {
        when(airplaneService.findById(anyLong())).thenThrow(new NoSuchElementException("Airplane not found"));

        mockMvc.perform(get("/api/airplanes/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Airplane not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(airplaneService, times(1)).findById(1L);
    }

    @Test
    void shouldCreateAirplane() throws Exception {
        AirplaneRequest request = new AirplaneRequest();
        request.setCode("B737");
        request.setName("Boeing 737");
        request.setCapacity(3);
        request.setSerialNumber("SN-123");
        Airplane airplane = new Airplane();
        AirplaneResponse response = new AirplaneResponse();

        when(airplaneMapper.toAirplane(any(AirplaneRequest.class))).thenReturn(airplane);
        when(airplaneService.save(any(Airplane.class))).thenReturn(airplane);
        when(airplaneMapper.toResponse(any(Airplane.class))).thenReturn(response);

        mockMvc.perform(post("/api/airplanes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detail").value("Airplane created"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(airplaneService, times(1)).save(any(Airplane.class));
    }

    @Test
    void shouldReturnBadRequestIfCreateAirplaneInvalid() throws Exception {
        mockMvc.perform(post("/api/airplanes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(airplaneService, never()).save(any(Airplane.class));
    }

    @Test
    void shouldUpdateAirplane() throws Exception {
        AirplaneRequest request = new AirplaneRequest();
        request.setCapacity(3);
        request.setName("Boeing 737");
        request.setCode("B737");
        request.setSerialNumber("SN-123");
        Airplane airplane = new Airplane();
        AirplaneResponse response = new AirplaneResponse();

        when(airplaneService.findById(anyLong())).thenReturn(airplane);
        when(airplaneService.save(any(Airplane.class))).thenReturn(airplane);
        when(airplaneMapper.toResponse(any(Airplane.class))).thenReturn(response);

        mockMvc.perform(put("/api/airplanes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Airplane updated"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(airplaneService, times(1)).findById(1L);
        verify(airplaneService, times(1)).save(any(Airplane.class));
    }

    @Test
    void shouldReturnNotFoundIfUpdateAirplaneNotExists() throws Exception {
        AirplaneRequest request = new AirplaneRequest();
        request.setCapacity(3);
        request.setName("Boeing 737");
        request.setCode("B737");
        request.setSerialNumber("SN-123");
        when(airplaneService.findById(anyLong())).thenThrow(new NoSuchElementException("Airplane not found"));

        mockMvc.perform(put("/api/airplanes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Airplane not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(airplaneService, times(1)).findById(1L);
        verify(airplaneService, never()).save(any(Airplane.class));
    }

    @Test
    void shouldReturnBadRequestIfUpdateAirplaneInvalid() throws Exception {
        mockMvc.perform(put("/api/airplanes/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(airplaneService, never()).save(any(Airplane.class));
    }

    @Test
    void shouldDeleteAirplane() throws Exception {
        Airplane airplane = new Airplane();
        AirplaneResponse response = new AirplaneResponse();

        when(airplaneService.findById(anyLong())).thenReturn(airplane);
        when(airplaneMapper.toResponse(any(Airplane.class))).thenReturn(response);

        mockMvc.perform(delete("/api/airplanes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Airplane deleted"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(airplaneService, times(1)).findById(1L);
        verify(airplaneService, times(1)).delete(any(Airplane.class));
    }

    @Test
    void shouldReturnNotFoundIfDeleteAirplaneNotExists() throws Exception {
        when(airplaneService.findById(anyLong())).thenThrow(new NoSuchElementException("Airplane not found"));

        mockMvc.perform(delete("/api/airplanes/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Airplane not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(airplaneService, times(1)).findById(1L);
        verify(airplaneService, never()).delete(any(Airplane.class));
    }

    @Test
    void shouldRestoreAirplane() throws Exception {
        Airplane airplane = new Airplane();
        AirplaneResponse response = new AirplaneResponse();

        when(airplaneService.findByIdWithDeleted(anyLong())).thenReturn(airplane);
        when(airplaneMapper.toResponse(any(Airplane.class))).thenReturn(response);

        mockMvc.perform(put("/api/airplanes/{id}/restore", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("Airplane restored"))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty());

        verify(airplaneService, times(1)).findByIdWithDeleted(1L);
        verify(airplaneService, times(1)).restore(any(Airplane.class));
    }

    @Test
    void shouldReturnBadRequestIfRestoreAirplaneNotDeleted() throws Exception {
        when(airplaneService.findByIdWithDeleted(anyLong())).thenReturn(new Airplane());
        IllegalStateException exception = new IllegalStateException("Airplane is not deleted");
        doThrow(exception).when(airplaneService).restore(any(Airplane.class));

        mockMvc.perform(put("/api/airplanes/{id}/restore", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Airplane is not deleted"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(airplaneService, times(1)).findByIdWithDeleted(1L);
        verify(airplaneService, times(1
        )).restore(any(Airplane.class));
    }
}