package pl.sgorski.AirLink.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.sgorski.AirLink.dto.ProfileRequest;
import pl.sgorski.AirLink.dto.ProfileResponse;
import pl.sgorski.AirLink.model.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    void updateProfile(ProfileRequest request, @MappingTarget Profile profile);

    @Mapping(target = "email", source = "user.email")
    ProfileResponse toResponse(Profile profile);
}
