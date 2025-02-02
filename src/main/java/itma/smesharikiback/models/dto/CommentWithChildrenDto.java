package itma.smesharikiback.models.dto;

import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentWithChildrenDto {
    private Long id;
    private Smesharik smesharik;
    private Post post;
    private LocalDateTime creationDate;
    private Comment parentComment;
    private String text;
    private boolean hasChildren;
    private Long countCarrots;
}