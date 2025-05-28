package pl.sgorski.AirLink.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pl.sgorski.AirLink.model.Role;
import pl.sgorski.AirLink.model.User;
import pl.sgorski.AirLink.service.RoleService;
import pl.sgorski.AirLink.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final RoleService roleService;
    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Role> roles = initRoles();
        List<User> users = initUsers(roles);
    }

    private List<Role> initRoles() {
        if(roleService.count() > 0) return new ArrayList<>();
        List<Role> roles = new ArrayList<>();
        Role userRole = new Role();
        userRole.setName("USER");
        roles.add(userRole);
        roleService.save(userRole);
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        roles.add(adminRole);
        roleService.save(adminRole);
        return roles;
    }

    private List<User> initUsers(List<Role> roles) {
        if(userService.count() > 0) return new ArrayList<>();
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setEmail("admin@sg.com");
        user.setPassword("password");
        user.setRole(roles.get(1));
        users.add(user);
        userService.save(user);
        return users;
    }
}
