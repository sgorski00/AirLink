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

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    @Test
    void shouldFindUserByEmail() {
        String email = "test@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        User result = userService.findByEmail(email);

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void shouldThrowIfUserByEmailNotFound() {
        String email = "test@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.findByEmail(email));
    }

    @Test
    void shouldCountUsers() {
        userService.count();
        verify(userRepository, times(1)).count();
    }

    @Test
    void shouldFindAllUsers() {
        userService.findAll();
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldFindUserById() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        User result = userService.findById(userId);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldThrowIfUserByIdNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.findById(userId));
    }
}
