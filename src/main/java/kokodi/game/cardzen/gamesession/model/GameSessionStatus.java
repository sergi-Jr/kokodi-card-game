package kokodi.game.cardzen.gamesession.model;

import lombok.Getter;

@Getter
public enum GameSessionStatus {
    WAITING_PLAYERS("wait for players"),
    IN_PROGRESS("game in progress"),
    FINISHED("finished");

    private final String description;

    GameSessionStatus(String description) {
        this.description = description;
    }
}
