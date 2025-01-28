package itma.smesharikiback.response;

import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BanResponse {
    private Long id;
    private String reason;
    private Long smesharik;
    private Long post;
    private LocalDateTime endDate;
    private Long comment;
    private LocalDateTime creationDate;
}
