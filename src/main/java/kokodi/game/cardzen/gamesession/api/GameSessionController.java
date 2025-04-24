package kokodi.game.cardzen.gamesession.api;

import kokodi.game.cardzen.gamesession.dal.GameSessionService;
import kokodi.game.cardzen.gamesession.dto.GamePlayTurnDto;
import kokodi.game.cardzen.gamesession.dto.GameSessionDto;
import kokodi.game.cardzen.gamesession.dto.GameSessionStorageInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/games", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class GameSessionController {

    private final GameSessionService gameSessionService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameSessionDto createGame(Authentication authentication) {
        String username = authentication.getName();
        return gameSessionService.createGame(username);
    }

    @PostMapping(path = "/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public void joinGame(@PathVariable Long gameId, Authentication authentication) {
        String username = authentication.getName();
        gameSessionService.joinGame(gameId, username);
    }

    @GetMapping(path = "/{gameId}")
    public GameSessionStorageInfoDto startGame(@PathVariable Long gameId) {
        String topic = "/topic/" + gameId;
        GameSessionStorageInfoDto dto = gameSessionService.startGame(gameId);
        messagingTemplate.convertAndSend(topic, dto);
        return dto;
    }

    @PostMapping(path = "/{gameId}/turn")
    @ResponseStatus(HttpStatus.CREATED)
    public GameSessionStorageInfoDto playTurn(@PathVariable Long gameId,
                                              @RequestBody GamePlayTurnDto turnDto,
                                              Authentication authentication) {
        String topic = "/topic/" + gameId;
        String username = authentication.getName();
        GameSessionStorageInfoDto dto = gameSessionService.playTurn(gameId, turnDto, username);
        messagingTemplate.convertAndSend(topic, dto);
        return dto;
    }

    @GetMapping(path = "/{gameId}/info")
    public GameSessionStorageInfoDto getGameInfo(@PathVariable Long gameId) {
        return gameSessionService.getGameInfo(gameId);
    }
}
