package itma.smesharikiback.presentation.dto.response;

import itma.smesharikiback.domain.model.SmesharikRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SmesharikResponse {
    @NotNull(message = "Login не может быть null.")
    @NotBlank(message = "Login не может быть пустым.")
    @Size(min = 4, max = 64, message = "Длина login от 4 до 64.")
    private String login;

    @NotNull(message = "Name не может быть null.")
    @NotBlank(message = "Name не может быть пустым.")
    @Size(min = 4, max = 64, message = "Длина name от 4 до 64.")
    private String name;

    @NotNull(message = "email не может быть null.")
    @NotBlank(message = "email не может быть пустым.")
    @Size(min = 4, max = 128, message = "Длина email от 4 до 128.")
    private String email;

    private SmesharikRole role;
    private Boolean isOnline;
    private String color;
}













