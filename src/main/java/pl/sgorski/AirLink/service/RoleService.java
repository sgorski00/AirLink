package pl.sgorski.AirLink.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sgorski.AirLink.model.Role;
import pl.sgorski.AirLink.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public void save(Role role) {
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
