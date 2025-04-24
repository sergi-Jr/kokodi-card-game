package kokodi.game.cardzen.gamesession.dto;

import kokodi.game.cardzen.gamesession.model.GameSessionStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSessionDto {
    private Long id;
    private int playersCount;
    private GameSessionStatus status;
}
