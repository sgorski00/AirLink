package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Airplane;
import pl.sgorski.AirLink.repository.AirplaneRepository;

import java.util.List;

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
}
