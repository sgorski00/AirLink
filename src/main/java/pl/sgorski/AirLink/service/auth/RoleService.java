package pl.sgorski.AirLink.service.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.auth.Role;
import pl.sgorski.AirLink.repository.auth.RoleRepository;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @CachePut(value = "roles", key = "#result.name")
    public Role save(@Valid Role role) {
        log.debug("Saving new role {}", role);
        return roleRepository.save(role);
    }

    @Cacheable(value = "roles", key = "#name")
    public Role findByName(String name) {
        return roleRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new IllegalStateException("Role with name " + name + " not found")
        );
    }

    public long count() {
        return roleRepository.count();
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }
}
