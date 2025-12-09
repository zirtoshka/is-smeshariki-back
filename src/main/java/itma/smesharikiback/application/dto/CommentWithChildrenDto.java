package itma.smesharikiback.application.dto;

import itma.smesharikiback.domain.model.Comment;
import itma.smesharikiback.domain.model.Post;
import itma.smesharikiback.domain.model.Smesharik;
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












