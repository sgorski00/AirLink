package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.repository.AirplaneRepository;

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

    public Airplane findById(Long id) {
        log.debug("Finding airplane with id: {}", id);
        return airplaneRepository.findById(id).orElseThrow(
            () -> new NoSuchElementException("Airplane with id " + id + " not found")
        );
    }
}
