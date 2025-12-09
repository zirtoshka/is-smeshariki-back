package itma.smesharikiback.presentation.dto.request.smesharik;

import itma.smesharikiback.presentation.validation.Login;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SmesharikSignInRequest {
    @NotNull(message = "Поле login обязательно.")
    @NotBlank(message = "Поле login не может быть пустым.")
    @Login(message = "Логин должен содержать 4-64 допустимых символа.")
    private String login;

    @NotNull(message = "Поле password обязательно.")
    @NotBlank(message = "Поле password не может быть пустым.")
    @Size(max = 255, message = "Длина password не должна превышать 255 символов.")
    private String password;
}
