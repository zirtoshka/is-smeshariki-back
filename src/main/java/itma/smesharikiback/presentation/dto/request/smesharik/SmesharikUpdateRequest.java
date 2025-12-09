package itma.smesharikiback.presentation.dto.request.smesharik;

import itma.smesharikiback.presentation.validation.HexColor;
import itma.smesharikiback.presentation.validation.Login;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SmesharikUpdateRequest {
    @NotNull(message = "Поле login обязательно.")
    @NotBlank(message = "Поле login не может быть пустым.")
    @Login(message = "Логин должен содержать 4-64 допустимых символа.")
    private String login;

    @NotNull(message = "Поле name обязательно.")
    @NotBlank(message = "Поле name не может быть пустым.")
    @Size(min = 4, max = 64, message = "Длина name должна быть от 4 до 64 символов.")
    private String name;

    @NotNull(message = "Поле email обязательно.")
    @NotBlank(message = "Поле email не может быть пустым.")
    @Size(min = 4, max = 128, message = "Длина email должна быть от 4 до 128 символов.")
    private String email;

    @HexColor(message = "Цвет должен быть в формате #RRGGBB.")
    private String color;
}
