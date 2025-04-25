package kokodi.game.cardzen.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kokodi.game.cardzen.user.dal.UserMapper;
import kokodi.game.cardzen.user.dal.UserRepository;
import kokodi.game.cardzen.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import kokodi.game.cardzen.util.EntityGenerator;

import java.util.Map;
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityGenerator generator;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper userMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor wrongToken;

    private User wrong;

    private User user;

    @BeforeEach
    void setup() {
        wrong = generator.generateUser();
        user = generator.generateUser();
        token = jwt().jwt(builder -> builder.subject(user.getName()));
        wrongToken = jwt().jwt(b -> b.subject(wrong.getName()));
        repository.save(user);
        repository.save(wrong);
    }

    @Test
    @Order(1)
    public void testShow() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/" + user.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(user.getEmail()),
                v -> v.node("name").isEqualTo(user.getName())
        );
    }

    @Test
    @Order(2)
    public void testShowNoAuth() throws Exception {
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    void testCreate() throws Exception {
        User user1 = generator.generateUser();

        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userMapper.mapToCreateDTO(user1)));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        Optional<User> opActual = repository.findByName(user1.getName());

        assertThat(opActual).isNotNull();
        User actual = opActual.get();
        assertThat(user1.getEmail()).isEqualTo(actual.getEmail());
        assertThat(user1.getName()).isEqualTo(actual.getName());
    }

    @Test
    @Order(4)
    public void testUpdate() throws Exception {
        Map<String, String> data = Map.of("email", "trueTest@gmail.com", "name", "John");

        var request = patch("/api/users/" + user.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        Optional<User> opActual = repository.findById(user.getId());

        assertThat(opActual).isNotNull();
        User actual = opActual.get();
        assertThat(data).containsEntry("email", actual.getEmail());
        assertThat(data).containsEntry("name", actual.getName());
    }

    @Test
    @Order(5)
    public void testUpdateNoAuth() throws Exception {
        Map<String, String> data = Map.of("email", "trueTest@gmail.com", "firstName", "John");

        var request = patch("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(6)
    public void testUpdateForbidden() throws Exception {
        Map<String, String> data = Map.of("email", "trueTest@gmail.com", "firstName", "John");

        var request = patch("/api/users/" + user.getId()).with(wrongToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(7)
    public void testDelete() throws Exception {
        var request = delete("/api/users/" + user.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        User opActual = repository.findById(user.getId()).orElse(null);

        assertThat(opActual).isNull();
    }

    @Test
    @Order(8)
    public void testDeleteNoAuth() throws Exception {
        var request = delete("/api/users/" + user.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    public void testDeleteForbidden() throws Exception {
        var request = delete("/api/users/" + user.getId()).with(wrongToken);
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }
}
