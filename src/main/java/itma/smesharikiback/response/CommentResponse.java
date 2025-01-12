package itma.smesharikiback.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CommentResponse {
    private Long id;
    private Long smesharik;
    private Long post;
    private LocalDateTime creationDate;
    private Long parentComment;
    private String text;
}
