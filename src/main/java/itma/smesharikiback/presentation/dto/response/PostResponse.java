package itma.smesharikiback.presentation.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class PostResponse {
    protected Long id;
    protected String author;
    protected Boolean isDraft;
    protected String text;
    protected Boolean isPrivate = true;
    protected LocalDateTime publicationDate;
    protected String pathToImage;
    protected LocalDateTime creationDate;
}













