package kokodi.game.cardzen.util;

import kokodi.game.cardzen.user.model.User;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EntityGenerator {

    private final Faker faker;

    public User generateUser() {
        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getGames))
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getName), () -> {
                    String nickname = faker.internet().username();
                    return nickname.length() <= 14 ? nickname : nickname.substring(0, 14);
                })
                .supply(Select.field(User::getPassword), () -> faker.internet().password(3, 10))
                .create();
    }
}
