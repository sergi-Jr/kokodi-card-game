package kokodi.game.cardzen.gamesession.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import kokodi.game.cardzen.user.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "games")
@ToString(onlyExplicitlyIncluded = true)
public class GameSession {
    @Id
    @SequenceGenerator(name = "game_seq",
            sequenceName = "game_sequence",
            initialValue = 1, allocationSize = 20)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_seq")
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private GameSessionStatus status;

    @OneToMany(mappedBy = "gameSession", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameTurnLog> turnLogs = new LinkedList<>();

    public void addTurnLog(GameTurnLog log) {
        turnLogs.add(log);
        log.setGameSession(this);
    }

    public void removeTurnLog(GameTurnLog log) {
        turnLogs.remove(log);
        log.setGameSession(null);
    }
}
