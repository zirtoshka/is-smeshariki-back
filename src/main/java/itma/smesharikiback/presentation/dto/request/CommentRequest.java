package itma.smesharikiback.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommentRequest {

    @Positive(message = "Поле post должно быть положительным.")
    private Long post;

    @Positive(message = "Поле parentComment должно быть положительным.")
    private Long parentComment;

    @NotNull(message = "Текст комментария обязателен.")
    @NotBlank(message = "Текст комментария не может быть пустым.")
    private String text;
}
