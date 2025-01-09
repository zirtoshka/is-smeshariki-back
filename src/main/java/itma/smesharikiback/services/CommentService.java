package itma.smesharikiback.services;

import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.reposirories.CommentRepository;
import itma.smesharikiback.models.reposirories.PostRepository;
import itma.smesharikiback.models.reposirories.SmesharikRepository;
import itma.smesharikiback.requests.CommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommentService {
    protected CommentRepository commentRepository;
    protected PostRepository postRepository;
    protected SmesharikRepository smesharikRepository;

    public Comment create(CommentRequest content) {
        return createComment(content);
    }


    private Comment createComment(CommentRequest request) {
        Comment comment = new Comment();
        Optional<Post> post = postRepository.findById(request.getPostId());
        Optional<Comment> comment1 = commentRepository.findById(request.getParentCommentId());
        Optional<Smesharik> smesharik = smesharikRepository.findById(request.getSmesharikId());

        post.ifPresent(comment::setPost);
        comment1.ifPresent(comment::setParentComment);
        smesharik.ifPresent(comment::setSmesharik);
        comment.setText(request.getText());

        return comment;
    }
}
