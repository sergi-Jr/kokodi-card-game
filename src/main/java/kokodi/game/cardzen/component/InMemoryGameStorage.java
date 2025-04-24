package kokodi.game.cardzen.component;

import kokodi.game.cardzen.gamesession.model.GameSessionStorage;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryGameStorage {

    private ConcurrentHashMap<Long, GameSessionStorage> sessions = new ConcurrentHashMap<>();

    public void addGameSession(Long gameId, GameSessionStorage gameStorage) {
        sessions.put(gameId, gameStorage);
    }

    public Optional<GameSessionStorage> getStorageByGameId(Long gameId) {
        return Optional.ofNullable(sessions.get(gameId));
    }

    public void removeGame(Long gameId) {
        sessions.remove(gameId);
    }
}
