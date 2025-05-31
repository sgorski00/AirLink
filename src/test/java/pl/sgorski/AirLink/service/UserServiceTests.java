package pl.sgorski.AirLink.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.repository.auth.UserRepository;
import pl.sgorski.AirLink.service.auth.UserService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("testPassword123");

        userService.save(user);

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(user);
    }
}
