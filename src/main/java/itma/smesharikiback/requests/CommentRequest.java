package itma.smesharikiback.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommentRequest {

    @Min(value = 0, message = "Значение поля y должно быть больше 0")
    private Long post;

    @Min(value = 0, message = "Значение поля y должно быть больше 0")
    private Long parentComment;

    @NotNull(message = "текст не может быть null")
    @NotBlank(message = "комментарий не может быть пустым")
    private String text;


}
