package pl.sgorski.AirLink.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sgorski.AirLink.model.Airplane;

import java.util.List;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Long> {

    @NonNull
    @Query("SELECT a FROM Airplane a WHERE a.deletedAt IS NULL")
    List<Airplane> findAll();
}
