package kokodi.game.cardzen.user.dal;

import com.fasterxml.jackson.databind.ObjectMapper;
import kokodi.game.cardzen.exception.DuplicateResourceException;
import kokodi.game.cardzen.exception.ResourceNotFoundException;
import kokodi.game.cardzen.user.dto.UserCreateDto;
import kokodi.game.cardzen.user.dto.UserDto;
import kokodi.game.cardzen.user.dto.UserUpdateDto;
import kokodi.game.cardzen.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service("UserService")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;


    public UserDto getOne(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userMapper.toUserDto(userOptional.orElseThrow(() ->
                new ResourceNotFoundException("Entity with id `%s` not found".formatted(id))));
    }

    @Transactional
    public UserDto create(UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        try {
            User resultUser = userRepository.save(user);
            return userMapper.toUserDto(resultUser);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("User already exists");
        }
    }

    @Transactional
    public UserDto patch(UUID id, UserUpdateDto dto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Entity with id `%s` not found".formatted(id)));

        userMapper.updateWithNull(dto, user);

        try {
            User resultUser = userRepository.save(user);
            return userMapper.toUserDto(resultUser);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("User already exists");
        }
    }

    @Transactional
    public void delete(UUID id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
    }

    public UUID getIdByName(String name) {
        return userRepository.findByName(name).orElseThrow(() ->
                new ResourceNotFoundException("Entity with name `%s` not found".formatted(name))).getId();
    }
}
