package itma.smesharikiback.presentation.dto.request;

import itma.smesharikiback.presentation.validation.Login;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BanRequest {

    @NotNull(message = "Причина бана обязательна.")
    @NotBlank(message = "Причина бана не может быть пустой.")
    private String reason;

    @Login(message = "Поле smesharik должно содержать корректный логин.")
    private String smesharik;

    @Positive(message = "Идентификатор поста должен быть положительным.")
    private Long post;

    @Positive(message = "Идентификатор комментария должен быть положительным.")
    private Long comment;

    private LocalDateTime endDate = LocalDateTime.now().plusHours(1);

    private LocalDateTime creationDate = LocalDateTime.now();
}
