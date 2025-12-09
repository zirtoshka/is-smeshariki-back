package itma.smesharikiback.presentation.dto.request.smesharik;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SmesharikChangePasswordRequest {
    @NotNull(message = "Поле oldPassword обязательно.")
    @NotBlank(message = "Поле oldPassword не может быть пустым.")
    @Size(max = 255, message = "Длина oldPassword не должна превышать 255 символов.")
    private String oldPassword;

    @NotNull(message = "Поле newPassword обязательно.")
    @NotBlank(message = "Поле newPassword не может быть пустым.")
    @Size(max = 255, message = "Длина newPassword не должна превышать 255 символов.")
    private String newPassword;
}
