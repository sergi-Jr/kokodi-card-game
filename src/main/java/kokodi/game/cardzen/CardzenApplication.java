package kokodi.game.cardzen;

import net.datafaker.Faker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CardzenApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardzenApplication.class, args);
    }

    @Bean
    public Faker faker() {
        return new Faker();
    }
}
