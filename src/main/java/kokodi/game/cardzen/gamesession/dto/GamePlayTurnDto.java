package kokodi.game.cardzen.gamesession.dto;

import kokodi.game.cardzen.card.model.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GamePlayTurnDto {
    private UUID activePlayerId;
    private UUID targetPlayerId;
    private Card card;
}
