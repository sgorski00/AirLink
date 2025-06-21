package pl.sgorski.AirLink.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import pl.sgorski.AirLink.dto.auth.LoginRequest;
import pl.sgorski.AirLink.dto.auth.LoginResponse;
import pl.sgorski.AirLink.dto.auth.RegisterRequest;
import pl.sgorski.AirLink.dto.auth.RegisterResponse;
import pl.sgorski.AirLink.mapper.RegistrationMapper;
import pl.sgorski.AirLink.model.Profile;
import pl.sgorski.AirLink.model.auth.Role;
import pl.sgorski.AirLink.model.auth.User;
import pl.sgorski.AirLink.service.MailService;
import pl.sgorski.AirLink.service.ProfileService;

@Log4j2
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final RoleService roleService;
    private final ProfileService profileService;
    private final RegistrationMapper registrationMapper;
    private final MailService mailService;
    private final TemplateEngine templateEngine;

    public LoginResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserDetails user = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }

    public RegisterResponse register(RegisterRequest request) {
        Role defaultRole = roleService.findByName("USER");
        Profile profile = new Profile();
        profile = profileService.save(profile);
        log.debug("Found role: {}", defaultRole.getName());
        User user = registrationMapper.toUser(request, defaultRole);
        user.setProfile(profile);
        log.debug("Registering user with empty profile: {}", user);
        User savedUser = userService.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());
        mailService.sendEmail(
                savedUser.getEmail(),
                "Welcome to AirLink",
                templateEngine.process("welcome-email", new Context())
        );
        return registrationMapper.toResponse(savedUser);
    }
}
