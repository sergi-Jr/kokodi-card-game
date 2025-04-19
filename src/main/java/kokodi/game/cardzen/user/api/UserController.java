package kokodi.game.cardzen.user.api;

import jakarta.validation.Valid;
import kokodi.game.cardzen.user.dal.UserService;
import kokodi.game.cardzen.user.dto.UserCreateDto;
import kokodi.game.cardzen.user.dto.UserDto;
import kokodi.game.cardzen.user.dto.UserUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@EnableMethodSecurity
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getOne(@PathVariable UUID id) {
        return userService.getOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserCreateDto dto) {
        return userService.create(dto);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@UserService.getIdByName(authentication.name) == #id")
    public UserDto patch(@PathVariable UUID id, @Valid @RequestBody UserUpdateDto dto) throws IOException {
        return userService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@UserService.getIdByName(authentication.name) == #id")
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }
}
