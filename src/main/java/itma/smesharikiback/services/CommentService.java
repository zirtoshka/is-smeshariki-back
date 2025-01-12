package itma.smesharikiback.services;

import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.reposirories.CommentRepository;
import itma.smesharikiback.models.reposirories.PostRepository;
import itma.smesharikiback.requests.CommentRequest;
import itma.smesharikiback.response.CommentResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class CommentService {
    private final SmesharikService smesharikService;
    protected CommentRepository commentRepository;
    protected PostRepository postRepository;

    public CommentResponse create(CommentRequest request) throws GeneralException {
        Comment comment = new Comment();
        Optional<Post> post = Optional.empty();
        Optional<Comment> comment1 = Optional.empty();
        if (request.getPostId() != null) post = postRepository.findById(request.getPostId());
        if (request.getParentCommentId() != null) comment1 = commentRepository.findById(request.getParentCommentId());

        HashMap<String, String> map = new HashMap<>();
        if ((comment1.isPresent() && post.isPresent()) ) {
            map.put("message", "Должно быть выставлено 1 из 2 параметров: parentComment, post.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, map);
        }
        if ((comment1.isEmpty() && post.isEmpty()) ) {
            map.put("message", "Не было найдено связанного post или comment.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, map);
        }

        post.ifPresent(comment::setPost);
        comment1.ifPresent(comment::setParentComment);
        comment.setText(request.getText());
        comment.setCreationDate(new Timestamp(new Date().getTime()).toLocalDateTime());
        comment.setSmesharik(smesharikService.getCurrentSmesharik());

        return buildResponse(commentRepository.save(comment));
    }



    private CommentResponse buildResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        if (comment.getParentComment() != null) response.setParentComment(comment.getParentComment().getId());
        if (comment.getPost() != null) response.setPost(comment.getPost().getId());

        return response
                .setId(comment.getId())
                .setCreationDate(comment.getCreationDate())
                .setSmesharik(comment.getSmesharik().getId())
                .setText(comment.getText());
    }

    public CommentResponse get(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "Комментарий не был найден");
            throw new GeneralException(HttpStatus.NOT_FOUND, map);
        }
        return buildResponse(comment.get());
    }
}
