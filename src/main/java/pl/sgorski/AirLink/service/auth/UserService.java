package pl.sgorski.AirLink.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.repository.auth.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @CachePut(value = "users", key = "#result.email")
    public User save(User user) {
        log.debug("Saving new user {}", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public long count() {
        return userRepository.count();
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("User not found with id: " + id)
        );
    }
}
