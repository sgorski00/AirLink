package pl.sgorski.AirLink.repository.auth;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sgorski.AirLink.model.auth.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @NonNull
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL")
    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND u.email = :email")
    Optional<User> findByEmail(String email);
}
