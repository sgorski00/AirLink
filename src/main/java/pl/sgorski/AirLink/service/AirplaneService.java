package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.repository.AirplaneRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;

    public Airplane save(Airplane airplane) {
        return airplaneRepository.save(airplane);
    }

    public List<Airplane> findAll() {
        return airplaneRepository.findAll();
    }

    public Page<Airplane> findAll(Pageable pageable) {
        return airplaneRepository.findAll(pageable);
    }

    public Airplane findById(Long id) {
        log.debug("Finding airplane with id: {}", id);
        return airplaneRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("Airplane with id " + id + " not found")
        );
    }

    public Airplane findByIdWithDeleted(Long id) {
        return airplaneRepository.findByIdWithDeleted(id).orElseThrow(
            () -> new NoSuchElementException("Airplane with id " + id + " not found")
        );
    }

    public Airplane findByIdWithFlights(Long id) {
        return airplaneRepository.findByIdWithFlights(id).orElseThrow(
            () -> new NoSuchElementException("Airplane with id " + id + " not found")
        );
    }

    public void delete(Airplane airplane) {
        log.debug("Deleting airplane with id: {}", airplane.getId());
        airplane.delete();
        airplaneRepository.save(airplane);
    }

    public void restore(Airplane airplane) {
        log.debug("Restoring airplane with id: {}", airplane.getId());
        airplane.restore();
        airplaneRepository.save(airplane);
    }
}
