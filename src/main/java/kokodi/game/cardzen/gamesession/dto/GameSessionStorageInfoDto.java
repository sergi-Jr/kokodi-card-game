package kokodi.game.cardzen.gamesession.dto;

import kokodi.game.cardzen.card.model.Card;
import kokodi.game.cardzen.gamesession.model.GameSessionStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class GameSessionStorageInfoDto {
    private int playersCount;
    private Map<UUID, Integer> playersWithPoints;
    private GameSessionStatus status;
    private Card topDeckCard;
    private int currentDeckSize;
    private UUID activePlayerId;
}
