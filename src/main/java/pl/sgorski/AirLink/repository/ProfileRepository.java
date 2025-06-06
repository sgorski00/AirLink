package pl.sgorski.AirLink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sgorski.AirLink.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

}
