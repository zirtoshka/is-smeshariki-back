package itma.smesharikiback.presentation.dto.request.smesharik;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SmesharikChangePasswordRequest {
    @NotNull(message = "oldPassword не может быть null.")
    @NotBlank(message = "oldPassword не может быть пустым.")
    @Size(max = 255, message = "Длина oldPassword должна быть не более 255 символов.")
    private String oldPassword;

    @NotNull(message = "password не может быть null.")
    @NotBlank(message = "password не может быть пустым.")
    @Size(max = 255, message = "Длина password должна быть не более 255 символов.")
    private String newPassword;
}













