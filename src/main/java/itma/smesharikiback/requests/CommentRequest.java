package itma.smesharikiback.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommentRequest {

    @NotNull(message = "author's id не может быть null")
    @Min(value = 0, message = "Значение поля y должно быть больше 0")
    private Long smesharikId;


    @NotNull(message = "пост не может быть null")
    @Min(value = 0, message = "Значение поля y должно быть больше 0")
    private Long postId;


    @Min(value = 0, message = "Значение поля y должно быть больше 0")
    private Long parentCommentId;


    @NotNull(message = "текст не может быть null")
    @NotBlank(message = "комментарий не может быть пустым")
    private String text;




}
