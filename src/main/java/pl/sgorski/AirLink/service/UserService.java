package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.User;
import pl.sgorski.AirLink.repository.UserRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User " + email + " not found")
        );
    }

    public User save(User user) {
        log.info("Saving user {}", user);
        return userRepository.save(user);
    }

    public long count() {
        return userRepository.count();
    }
}
