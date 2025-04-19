package kokodi.game.cardzen.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDto {
    @Size(min = 1, max = 14)
    @NotBlank(message = "Field cannot be empty")
    private JsonNullable<String> name;

    @Email(message = "Wrong email format",
            regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private JsonNullable<String> email;

    @Size(min = 3, max = 100)
    private JsonNullable<String> password;
}
