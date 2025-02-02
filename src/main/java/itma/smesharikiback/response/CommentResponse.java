package itma.smesharikiback.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CommentResponse {
    protected Long id;
    protected String smesharik;
    protected Long post;
    protected LocalDateTime creationDate;
    protected Long parentComment;
    protected String text;
}
