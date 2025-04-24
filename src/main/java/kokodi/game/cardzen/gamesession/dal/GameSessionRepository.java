package kokodi.game.cardzen.gamesession.dal;

import kokodi.game.cardzen.gamesession.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {
}
