package kokodi.game.cardzen.gamesession.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "turns")
@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)
public class GameTurnLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String cardType;

    private int cardValue;

    private UUID activeUserId;

    private UUID targetUserId;

    private Integer targetUserPoints;

    @ManyToOne
    @JoinColumn(name = "gameSession_id")
    private GameSession gameSession;
}

