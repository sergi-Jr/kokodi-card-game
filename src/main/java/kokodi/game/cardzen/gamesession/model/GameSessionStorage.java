package kokodi.game.cardzen.gamesession.model;

import kokodi.game.cardzen.card.model.Card;
import kokodi.game.cardzen.utils.DeckShuffler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

@Getter
@Setter
public class GameSessionStorage {
    private int playersCount;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Stack<Card> deck;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private LinkedList<UUID> usersIdQueue = new LinkedList<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<UUID, Integer> userIdsWithPoints;

    private GameSessionStatus status;

    public void addUserId(UUID userId) {
        usersIdQueue.add(userId);
    }

    public void fillDeck(int deckSize, int uniqueCardsCount) {
        deck = new DeckShuffler(deckSize).shuffleDeck(uniqueCardsCount);
    }

    public void createUsersTurnsQueue() {
        userIdsWithPoints = new HashMap<>();
        for (var id : usersIdQueue) {
            userIdsWithPoints.put(id, 0);
        }

        var tempUsersIdsList = List.copyOf(usersIdQueue);
        while (usersIdQueue.size() < 100) { //move magic number to .prop file
            usersIdQueue.addAll(tempUsersIdsList);
        }
    }

    public UUID getActivePlayerId() {
        return usersIdQueue.getFirst();
    }

    public Card getTopDeckCard() {
        return deck.peek();
    }

    public Map<UUID, Integer> getUserIdsWithPoints() {
        return Map.copyOf(userIdsWithPoints);
    }

    public int getCurrentDeckSize() {
        return deck.size();
    }

    public GameTurnLog playCard(Card card, UUID targetPlayerId) {
        usersIdQueue.pollFirst();
        deck.pop();

        GameTurnLog turnLog = new GameTurnLog();
        turnLog.setCardType(card.getName());
        turnLog.setCardValue(card.getValue());
        turnLog.setTargetUserId(targetPlayerId);

        Integer c = null;
        switch (card.getName()) {
            case "points":
                c = userIdsWithPoints.compute(targetPlayerId, (k, currentPoints)
                        -> currentPoints + card.getValue());
                break;
            case "block":
                usersIdQueue.pollFirst();
                break;
            case "steal":
                c = userIdsWithPoints.compute(targetPlayerId, (k, currentPoints)
                        -> Math.max(currentPoints - card.getValue(), 0));
                break;
            case "doubleDown":
                c = userIdsWithPoints.compute(targetPlayerId, (k, currentPoints)
                        -> Math.min(currentPoints * card.getValue(), 30));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + card.getName());
        }
        turnLog.setTargetUserPoints(c);
        status = isSomebodyWon() ? GameSessionStatus.FINISHED : status;
        return turnLog;
    }

    private boolean isSomebodyWon() {
        return userIdsWithPoints
                .entrySet()
                .stream()
                .anyMatch(en -> en.getValue() >= 30);
    }
}
