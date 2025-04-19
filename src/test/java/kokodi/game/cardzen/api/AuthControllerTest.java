package kokodi.game.cardzen.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import kokodi.game.cardzen.authentication.model.AuthRequest;
import kokodi.game.cardzen.user.dal.UserMapper;
import kokodi.game.cardzen.user.dal.UserRepository;
import kokodi.game.cardzen.user.dto.UserCreateDto;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserMapper userMapper;

    private AuthRequest generateAuthRequest(String name, String password) {
        AuthRequest request = new AuthRequest();
        request.setUsername(name);
        request.setPassword(password);
        return request;
    }

    @Test
    @Order(1)
    public void testLoginSuccess() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("test@gmail.com");
        dto.setName("supernickname");
        dto.setPassword("qwerty");
        userRepository.saveAndFlush(userMapper.toEntity(dto));

        AuthRequest requestBody = generateAuthRequest("supernickname", "qwerty");

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody));
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = response.getResponse().getContentAsString();

        assertThat(body).isNotNull();
    }

    @Test
    @Order(2)
    public void testLoginFailure() throws Exception {
        AuthRequest requestBody = generateAuthRequest("wrongEmail@gmail.com", "password");

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
