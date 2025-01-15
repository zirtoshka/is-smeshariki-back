package itma.smesharikiback.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentWithChildrenDto {
    private Long id;
    private Long smesharik;
    private Long post;
    private LocalDateTime creationDate;
    private Long parentComment;
    private String text;
    private boolean hasChildren;
}