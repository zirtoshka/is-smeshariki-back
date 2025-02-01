package itma.smesharikiback.requests.smesharik;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SmesharikSignInRequest {
    @NotNull(message = "login не может быть null.")
    @NotBlank(message = "login не может быть пустым.")
    @Size(min = 4, max = 64, message = "Длина login от 4 до 64.")
    private String login;

    @NotNull(message = "password не может быть null.")
    @NotBlank(message = "password не может быть пустым.")
    @Size(max = 255, message = "Длина password должна быть не более 255 символов.")
    private String password;
}
