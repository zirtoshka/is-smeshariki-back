package itma.smesharikiback.requests;

import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BanRequest {

    @NotNull(message = "reason не может быть null")
    @NotBlank(message = "reason не может быть пустой")
    private String reason;

    private Long smesharik;
    private Long post;
    private Long comment;

    private LocalDateTime endDate = LocalDateTime.now().plusHours(1);

    private LocalDateTime creationDate = LocalDateTime.now();
}
