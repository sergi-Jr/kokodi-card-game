package kokodi.game.cardzen.user.dal;

import kokodi.game.cardzen.mapper.JsonNullableMapper;
import kokodi.game.cardzen.mapper.ReferenceMapper;
import kokodi.game.cardzen.user.dto.UserCreateDto;
import kokodi.game.cardzen.user.dto.UserDto;
import kokodi.game.cardzen.user.dto.UserUpdateDto;
import kokodi.game.cardzen.user.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {
    @Autowired
    private PasswordEncoder encoder;

    @BeforeMapping
    public void encryptPassword(UserCreateDto dto) {
        String password = dto.getPassword();
        dto.setPassword(encoder.encode(password));
    }

    public abstract User toEntity(UserCreateDto dto);

    public abstract User toEntity(UserDto userDto);

    public abstract UserDto toUserDto(User user);

    public abstract void updateWithNull(UserUpdateDto userDto, @MappingTarget User user);

    public abstract UserCreateDto mapToCreateDTO(User model);
}
