package kokodi.game.cardzen.utils;

import kokodi.game.cardzen.card.model.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class DeckShuffler {
    private final int deckSize;
    private final List<String> cardTypes = List.of("points", "block", "steal", "doubleDown");

    public DeckShuffler(int deckSize) {
        this.deckSize = deckSize;
    }

    public Stack<Card> shuffleDeck(int uniqueCardsCount) {
        List<Card> uniqueCards = createUniques(uniqueCardsCount);

        List<Card> cardList = new ArrayList<>();
        while (cardList.size() < deckSize) {
            cardList.addAll(uniqueCards);
        }
        cardList = cardList.subList(0, deckSize);
        Collections.shuffle(cardList);

        Stack<Card> deck = new Stack<>();
        deck.addAll(cardList);
        return deck;
    }

    private List<Card> createUniques(int uniqueCardsCount) {
        Random r = new Random();
        var resultList = new ArrayList<Card>();
        while (uniqueCardsCount > 0) {
            String cardType = cardTypes.get(r.nextInt(cardTypes.size()));
            int cardValue = switch (cardType) {
                case "points", "steal" -> r.nextInt(1, 6);
                case "block" -> 1;
                case "doubleDown" -> 2;
                default -> 0;
            };
            resultList.add(new Card(cardType, cardValue));
            uniqueCardsCount--;
        }
        return resultList;
    }
}
