package kokodi.game.cardzen.gamesession.dal;

import kokodi.game.cardzen.component.InMemoryGameStorage;
import kokodi.game.cardzen.exception.DuplicateResourceException;
import kokodi.game.cardzen.exception.ResourceNotFoundException;
import kokodi.game.cardzen.gamesession.dto.GamePlayTurnDto;
import kokodi.game.cardzen.gamesession.dto.GameSessionStorageInfoDto;
import kokodi.game.cardzen.gamesession.model.GameSession;
import kokodi.game.cardzen.gamesession.model.GameSessionStatus;
import kokodi.game.cardzen.gamesession.model.GameSessionStorage;
import kokodi.game.cardzen.gamesession.model.GameTurnLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameSessionStorageService {

    private final InMemoryGameStorage gameStorage;

    private final GameSessionMapper gameSessionMapper;

    public GameSessionStorage getStorageByGameId(Long gameId) {
        return gameStorage.getStorageByGameId(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game with id: %s not found".formatted(gameId)));
    }

    public GameSessionStorageInfoDto getGameSessionStorageInfo(Long gameId) {
        GameSessionStorage storage = gameStorage.getStorageByGameId(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game with id: %s not found".formatted(gameId)));
        return gameSessionMapper.toStorageInfoDto(storage);
    }

    @Transactional
    public void addNewGame(GameSession game) {
        GameSessionStorage storage = new GameSessionStorage();
        storage.setStatus(game.getStatus());
        storage.setPlayersCount(1);
        storage.addUserId(game.getUser().getId());

        gameStorage.addGameSession(game.getId(), storage);
    }

    @Transactional
    public void joinGame(Long gameId, UUID newPlayerId) {
        GameSessionStorage gameSessionStorage = gameStorage.getStorageByGameId(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game with id: %s not found".formatted(gameId)));
        if (gameSessionStorage.getUserIdsWithPoints().containsKey(newPlayerId)) {
            throw new DuplicateResourceException("You already in this game!");
        }
        synchronized (gameSessionStorage) {
            if (gameSessionStorage.getStatus() == GameSessionStatus.WAITING_PLAYERS
                    && gameSessionStorage.getPlayersCount() < 4) {

                var playersCount = gameSessionStorage.getPlayersCount();
                gameSessionStorage.setPlayersCount(++playersCount);
                gameSessionStorage.addUserId(newPlayerId);
            }
        }
    }

    public boolean startGame(Long gameId) {
        GameSessionStorage gameSessionStorage = gameStorage.getStorageByGameId(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game with id: %s not found".formatted(gameId)));

        synchronized (gameSessionStorage) {
            if (gameSessionStorage.getStatus() == GameSessionStatus.WAITING_PLAYERS
                    && gameSessionStorage.getPlayersCount() > 1) {
                gameSessionStorage.fillDeck(50, 10); //move magic numbers to .prop file
                gameSessionStorage.createUsersTurnsQueue();
                gameSessionStorage.setStatus(GameSessionStatus.IN_PROGRESS);
            } else {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public GameTurnLog playTurn(Long gameId, GamePlayTurnDto turnDto) {
        GameSessionStorage storage = gameStorage.getStorageByGameId(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game with id: %s not found".formatted(gameId)));

        synchronized (storage) {
            GameTurnLog turnLog = storage.playCard(turnDto.getCard(), turnDto.getTargetPlayerId());
            turnLog.setActiveUserId(turnDto.getActivePlayerId());
            return turnLog;
        }
    }

    @Transactional
    public void removeGameSession(Long gameId) {
        gameStorage.removeGame(gameId);
    }
}
