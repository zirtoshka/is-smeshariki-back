package itma.smesharikiback.response;

import itma.smesharikiback.models.Smesharik;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class PostResponse {
    private Long id;
    private Long author;
    private Boolean isDraft;
    private String text;
    private Boolean isPrivate = true;
    private LocalDateTime publicationDate;
    private String pathToImage;
    private LocalDateTime creationDate;
}
