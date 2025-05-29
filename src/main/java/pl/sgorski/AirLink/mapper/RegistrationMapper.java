package pl.sgorski.AirLink.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.sgorski.AirLink.dto.RegisterRequest;
import pl.sgorski.AirLink.dto.RegisterResponse;
import pl.sgorski.AirLink.model.auth.Role;
import pl.sgorski.AirLink.model.auth.User;

@Mapper(componentModel = "spring")
public interface RegistrationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", source = "role")
    User toUser(RegisterRequest request, Role role);

    @Mapping(target = "roleName", source = "role.name")
    RegisterResponse toResponse(User user);
}
