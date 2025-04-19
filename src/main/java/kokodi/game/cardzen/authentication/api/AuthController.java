package kokodi.game.cardzen.authentication.api;

import kokodi.game.cardzen.authentication.model.AuthRequest;
import kokodi.game.cardzen.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/login", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public String login(@RequestBody AuthRequest request) {
        var auth = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());
        authenticationManager.authenticate(auth);

        return jwtUtils.generateToken(request.getUsername());
    }
}
