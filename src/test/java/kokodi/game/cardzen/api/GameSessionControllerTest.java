package kokodi.game.cardzen.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kokodi.game.cardzen.card.model.Card;
import kokodi.game.cardzen.gamesession.dal.GameSessionRepository;
import kokodi.game.cardzen.gamesession.dal.GameSessionStorageService;
import kokodi.game.cardzen.gamesession.dto.GamePlayTurnDto;
import kokodi.game.cardzen.gamesession.dto.GameSessionDto;
import kokodi.game.cardzen.gamesession.model.GameSession;
import kokodi.game.cardzen.gamesession.model.GameSessionStatus;
import kokodi.game.cardzen.gamesession.model.GameSessionStorage;
import kokodi.game.cardzen.user.dal.UserRepository;
import kokodi.game.cardzen.user.model.User;
import kokodi.game.cardzen.util.EntityGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityGenerator generator;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameSessionStorageService gameSessionStorageService;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private User user;

    @BeforeEach
    void setup() {
        user = generator.generateUser();
        token = jwt().jwt(builder -> builder.subject(user.getName()));
        userRepository.save(user);
    }

    @Test
    public void testCreateGame() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/games").with(token))
                .andExpect(status().isCreated())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        GameSessionDto model = mapper.readValue(body, GameSessionDto.class);

        assertThat(model.getStatus()).isEqualTo(GameSessionStatus.WAITING_PLAYERS);

        Optional<GameSession> opActual = gameSessionRepository.findById(model.getId());
        assertThat(opActual).isNotNull();
    }

    @Test
    public void testJoinGame() throws Exception {
        GameSession session = new GameSession();
        session.setStatus(GameSessionStatus.WAITING_PLAYERS);
        user.addGame(session);
        gameSessionRepository.save(session);

        var user1 = generator.generateUser();
        var token1 = jwt().jwt(builder -> builder.subject(user1.getName()));
        userRepository.save(user1);

        gameSessionStorageService.addNewGame(session);

        mockMvc.perform(post("/api/games/" + session.getId()).with(token1))
                .andExpect(status().isOk());

        GameSessionStorage gameStorage = gameSessionStorageService.getStorageByGameId(session.getId());

        assertThat(gameStorage.getPlayersCount()).isEqualTo(2);
        assertThat(gameStorage.getStatus()).isEqualTo(GameSessionStatus.WAITING_PLAYERS);
    }

    @Test
    public void testStartGame() throws Exception {
        GameSession session = new GameSession();
        session.setStatus(GameSessionStatus.WAITING_PLAYERS);
        user.addGame(session);
        gameSessionRepository.save(session);

        var user1 = generator.generateUser();
        var token1 = jwt().jwt(builder -> builder.subject(user1.getName()));
        userRepository.save(user1);

        gameSessionStorageService.addNewGame(session);
        gameSessionStorageService.joinGame(session.getId(), user1.getId());

        MvcResult result = mockMvc.perform(get("/api/games/" + session.getId()).with(token1))
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("playersCount").isEqualTo(2),
                v -> v.node("playersWithPoints").isEqualTo(Map.of(user1.getId(), 0, user.getId(), 0)),
                v -> v.node("status").isEqualTo(GameSessionStatus.IN_PROGRESS),
                v -> v.node("topDeckCard").isNotNull(),
                v -> v.node("currentDeckSize").isEqualTo(50),
                v -> v.node("activePlayerId").isEqualTo(user.getId()));
    }

    @Test
    public void testPlayTurn() throws Exception {
        GameSession session = new GameSession();
        session.setStatus(GameSessionStatus.WAITING_PLAYERS);
        user.addGame(session);
        gameSessionRepository.save(session);

        var user1 = generator.generateUser();
        var token1 = jwt().jwt(builder -> builder.subject(user1.getName()));
        userRepository.save(user1);

        gameSessionStorageService.addNewGame(session);
        gameSessionStorageService.joinGame(session.getId(), user1.getId());
        gameSessionStorageService.startGame(session.getId());

        var jokerBlock = new Card("block", 1);
        var turnDto = new GamePlayTurnDto();
        turnDto.setCard(jokerBlock);
        turnDto.setActivePlayerId(user.getId());
        turnDto.setTargetPlayerId(user1.getId());

        var req = post("/api/games/" + session.getId() + "/turn").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(turnDto));
        MvcResult result = mockMvc.perform(req)
                .andExpect(status().isCreated())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("playersCount").isEqualTo(2),
                v -> v.node("playersWithPoints").isEqualTo(Map.of(user.getId(), 0, user1.getId(), 0)),
                v -> v.node("status").isEqualTo(GameSessionStatus.IN_PROGRESS),
                v -> v.node("topDeckCard").isNotNull(),
                v -> v.node("currentDeckSize").isEqualTo(49),
                v -> v.node("activePlayerId").isEqualTo(user.getId()));


        var jokerPoints = new Card("points", 4);
        turnDto = new GamePlayTurnDto();
        turnDto.setCard(jokerPoints);
        turnDto.setActivePlayerId(user.getId());
        turnDto.setTargetPlayerId(user.getId());

        req = post("/api/games/" + session.getId() + "/turn").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(turnDto));
        result = mockMvc.perform(req)
                .andExpect(status().isCreated())
                .andReturn();

        body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("playersCount").isEqualTo(2),
                v -> v.node("playersWithPoints").isEqualTo(Map.of(user.getId(), 4, user1.getId(), 0)),
                v -> v.node("status").isEqualTo(GameSessionStatus.IN_PROGRESS),
                v -> v.node("topDeckCard").isNotNull(),
                v -> v.node("currentDeckSize").isEqualTo(48),
                v -> v.node("activePlayerId").isEqualTo(user1.getId()));

        var jokerSteal = new Card("steal", 2);
        turnDto = new GamePlayTurnDto();
        turnDto.setCard(jokerSteal);
        turnDto.setActivePlayerId(user1.getId());
        turnDto.setTargetPlayerId(user.getId());

        req = post("/api/games/" + session.getId() + "/turn").with(token1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(turnDto));
        result = mockMvc.perform(req)
                .andExpect(status().isCreated())
                .andReturn();

        body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("playersCount").isEqualTo(2),
                v -> v.node("playersWithPoints").isEqualTo(Map.of(user.getId(), 2, user1.getId(), 0)),
                v -> v.node("status").isEqualTo(GameSessionStatus.IN_PROGRESS),
                v -> v.node("topDeckCard").isNotNull(),
                v -> v.node("currentDeckSize").isEqualTo(47),
                v -> v.node("activePlayerId").isEqualTo(user.getId()));

        var jokerDouble = new Card("doubleDown", 2);
        turnDto = new GamePlayTurnDto();
        turnDto.setCard(jokerDouble);
        turnDto.setActivePlayerId(user.getId());
        turnDto.setTargetPlayerId(user.getId());

        req = post("/api/games/" + session.getId() + "/turn").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(turnDto));
        result = mockMvc.perform(req)
                .andExpect(status().isCreated())
                .andReturn();

        body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("playersCount").isEqualTo(2),
                v -> v.node("playersWithPoints").isEqualTo(Map.of(user.getId(), 4, user1.getId(), 0)),
                v -> v.node("status").isEqualTo(GameSessionStatus.IN_PROGRESS),
                v -> v.node("topDeckCard").isNotNull(),
                v -> v.node("currentDeckSize").isEqualTo(46),
                v -> v.node("activePlayerId").isEqualTo(user1.getId()));
    }
}
