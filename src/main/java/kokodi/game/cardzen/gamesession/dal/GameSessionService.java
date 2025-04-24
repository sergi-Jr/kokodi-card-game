package kokodi.game.cardzen.gamesession.dal;

import kokodi.game.cardzen.exception.IllegalTurnAccept;
import kokodi.game.cardzen.exception.ResourceNotFoundException;
import kokodi.game.cardzen.exception.UnableToGameStart;
import kokodi.game.cardzen.gamesession.dto.GamePlayTurnDto;
import kokodi.game.cardzen.gamesession.dto.GameSessionDto;
import kokodi.game.cardzen.gamesession.dto.GameSessionStorageInfoDto;
import kokodi.game.cardzen.gamesession.model.GameSession;
import kokodi.game.cardzen.gamesession.model.GameSessionStatus;
import kokodi.game.cardzen.gamesession.model.GameTurnLog;
import kokodi.game.cardzen.user.dal.UserRepository;
import kokodi.game.cardzen.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameSessionService {

    private final GameSessionRepository gameSessionRepository;
    private final GameSessionMapper gameSessionMapper;
    private final UserRepository userRepository;
    private final GameSessionStorageService gameSessionStorageService;

    @Transactional
    public GameSessionDto createGame(String username) {
        User user = userRepository.findByName(username).orElseThrow(
                () -> new ResourceNotFoundException("User with name: %s not found".formatted(username)));
        GameSession game = new GameSession();
        game.setStatus(GameSessionStatus.WAITING_PLAYERS);
        user.addGame(game);
        gameSessionRepository.save(game);

        gameSessionStorageService.addNewGame(game);

        return gameSessionMapper.toGameSessionDto(game);
    }

    public void joinGame(Long gameId, String username) {
        GameSession game = gameSessionRepository.findById(gameId).orElseThrow(
                () -> new ResourceNotFoundException("Game with id: %s not found".formatted(gameId)));
        User user = userRepository.findByName(username).orElseThrow(
                () -> new ResourceNotFoundException("User with name: %s not found".formatted(username)));
        user.addGame(game);
        userRepository.save(user);
        gameSessionStorageService.joinGame(gameId, user.getId());
    }

    public GameSessionStorageInfoDto startGame(Long gameId) {
        GameSession game = gameSessionRepository.findById(gameId).orElseThrow(
                () -> new ResourceNotFoundException("Game with id: %s not found".formatted(gameId)));
        if (gameSessionStorageService.startGame(gameId)) {
            game.setStatus(GameSessionStatus.IN_PROGRESS);
            gameSessionRepository.save(game);
        } else {
            throw new UnableToGameStart("Unable to start the game");
        }
        return gameSessionStorageService.getGameSessionStorageInfo(gameId);
    }

    public GameSessionStorageInfoDto playTurn(Long gameId, GamePlayTurnDto turnDto, String username) {
        GameSession game = gameSessionRepository.findById(gameId).orElseThrow(
                () -> new ResourceNotFoundException("Game with id: %s not found".formatted(gameId)));
        if (game.getStatus() == GameSessionStatus.FINISHED) {
            throw new IllegalTurnAccept("Game is finished!");
        }

        UUID userId = userRepository.findByName(username).orElseThrow(
                () -> new ResourceNotFoundException("User with name: %s not found".formatted(username))).getId();

        GameSessionStorageInfoDto dto;
        if (userId.equals(turnDto.getActivePlayerId())) {
            GameTurnLog turnLog = gameSessionStorageService.playTurn(gameId, turnDto);
            game.addTurnLog(turnLog);
            dto = gameSessionStorageService.getGameSessionStorageInfo(gameId);

            if (dto.getStatus() == GameSessionStatus.FINISHED) {
                game.setStatus(GameSessionStatus.FINISHED);
                gameSessionStorageService.removeGameSession(gameId);
            }

            gameSessionRepository.save(game);
        } else {
            throw new IllegalTurnAccept("Not your turn!");
        }
        return dto;
    }

    public GameSessionStorageInfoDto getGameInfo(Long gameId) {
        return gameSessionStorageService.getGameSessionStorageInfo(gameId);
    }
}
