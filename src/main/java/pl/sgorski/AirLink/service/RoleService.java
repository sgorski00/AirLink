package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Role;
import pl.sgorski.AirLink.repository.RoleRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public void save(Role role) {
        log.debug("Saving new role {}", role);
        roleRepository.save(role);
    }

    public Role findByName(String name) {
        return roleRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new IllegalStateException("Role with name " + name + " not found")
        );
    }

    public long count() {
        return roleRepository.count();
    }
}
