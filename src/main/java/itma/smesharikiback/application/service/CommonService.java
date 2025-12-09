package itma.smesharikiback.application.service;

import itma.smesharikiback.domain.exception.AccessDeniedException;
import itma.smesharikiback.domain.exception.DomainException;
import itma.smesharikiback.domain.exception.ValidationException;
import itma.smesharikiback.domain.model.Comment;
import itma.smesharikiback.domain.model.Post;
import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.model.SmesharikRole;
import itma.smesharikiback.domain.repository.CommentRepository;
import itma.smesharikiback.domain.repository.PostRepository;
import itma.smesharikiback.domain.repository.SmesharikRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommonService {
    private final SmesharikRepository smesharikRepository;
    protected CommentRepository commentRepository;
    protected PostRepository postRepository;


    public Pair<Comment, Post> getParentCommentOrPost(Long commentId, Long postId) {
        HashMap<String, String> map = new HashMap<>();
        Optional<Post> postOpt = Optional.empty();
        Optional<Comment> commentOpt = Optional.empty();
        if (postId != null) {
            postOpt = postRepository.findById(postId);
        }
        if (commentId != null) {
            commentOpt = commentRepository.findById(commentId);
        }

        if ((commentOpt.isPresent() && postOpt.isPresent()) ) {
            map.put("message", "Exactly one of comment or post should be provided.");
            throw new DomainException(HttpStatus.NOT_FOUND, map);
        }
        if ((commentOpt.isEmpty() && postOpt.isEmpty()) ) {
            map.put("message", "Related post or comment not found.");
            throw new ValidationException(map);
        }
        Comment comment = commentOpt.orElse(null);
        Post post = postOpt.orElse(null);

        return Pair.of(comment, post);
    }

    public void checkIfAdmin() {
        HashMap<String, String> errors = new HashMap<>();
        if (!getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN)) {
            errors.put("message", "Access denied.");
            throw new AccessDeniedException(errors);
        }
    }

    public void checkIfDoctorOrAdmin() {
        HashMap<String, String> errors = new HashMap<>();
        if (!getCurrentSmesharik().getRole().equals(SmesharikRole.DOCTOR) &&
                !getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN)) {
            errors.put("message", "Access denied.");
            throw new AccessDeniedException(errors);
        }
    }

    public Smesharik getCurrentSmesharik() {
        var login = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByLogin(login);
    }

    public Smesharik getByLogin(String login) {
        return smesharikRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }
}

