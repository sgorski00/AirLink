package pl.sgorski.AirLink.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sgorski.AirLink.model.Reservation;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Override
    @Query("SELECT r FROM Reservation r WHERE r.deletedAt IS NULL")
    @NonNull
    List<Reservation> findAll();

    @Override
    @Query("SELECT r FROM Reservation r WHERE r.deletedAt IS NULL")
    @NonNull
    Page<Reservation> findAll(@NonNull Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.deletedAt IS NULL")
    Page<Reservation> findAllByUserId(Long userId, Pageable pageable);

    @Override
    @Query("SELECT r FROM Reservation r WHERE r.id = :id AND r.deletedAt IS NULL")
    @NonNull
    Optional<Reservation> findById(@NonNull Long id);

    @Query("SELECT r FROM Reservation r WHERE r.id = :id AND r.deletedAt IS NOT NULL")
    Optional<Reservation> findDeletedById(Long id);
}
