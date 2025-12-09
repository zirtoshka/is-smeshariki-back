package itma.smesharikiback.application.service;

import itma.smesharikiback.application.dto.CommentWithChildrenDto;
import itma.smesharikiback.application.mapper.DomainMapper;
import itma.smesharikiback.domain.exception.AccessDeniedException;
import itma.smesharikiback.domain.exception.DomainException;
import itma.smesharikiback.domain.model.Comment;
import itma.smesharikiback.domain.model.Post;
import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.repository.CommentRepository;
import itma.smesharikiback.presentation.dto.request.CommentRequest;
import itma.smesharikiback.presentation.dto.response.CommentResponse;
import itma.smesharikiback.presentation.dto.response.CommentWithChildrenResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommonService commonService;
    private final PsychoService psychoService;
    private final FriendService friendService;
    private final DomainMapper domainMapper;

    public CommentResponse create(CommentRequest request) {
        Pair<Comment, Post> pair = commonService.getParentCommentOrPost(request.getParentComment(), request.getPost());

        Comment comment = new Comment();
        Post post = pair.getRight();
        Comment parentComment = pair.getLeft();

        Smesharik smesharik;
        if (post == null) {
            smesharik = findPostAuthorByComment(parentComment);
        } else {
            smesharik = post.getAuthor();
        }

        if (!friendService.isFriendsOrAdmin(smesharik.getLogin(), commonService.getCurrentSmesharik().getLogin()) ||
                (post != null && (post.getIsDraft() || post.getIsPrivate()))) {
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "No permission to add a comment.");
            throw new DomainException(HttpStatus.NOT_FOUND, map);
        }

        comment.setPost(post);
        comment.setParentComment(parentComment);
        comment.setText(request.getText());
        comment.setCreationDate(new Timestamp(new Date().getTime()).toLocalDateTime());
        comment.setSmesharik(commonService.getCurrentSmesharik());
        CommentResponse commentResponse = domainMapper.toCommentResponse(commentRepository.save(comment));
        psychoService.addToCommentQueue(comment);

        return commentResponse;
    }

    public Smesharik findPostAuthorByComment(Comment comment) {
        if (comment.getParentComment() != null) {
            return findPostAuthorByComment(comment.getParentComment());
        }
        return comment.getPost().getAuthor();
    }

    public Smesharik findPostAuthorByComment(CommentWithChildrenDto comment) {
        if (comment.getParentComment() != null) {
            return findPostAuthorByComment(comment.getParentComment());
        }
        return comment.getPost().getAuthor();
    }

    public CommentResponse get(Long id) {
        Optional<CommentWithChildrenDto> comment = commentRepository.findByIdWithChildren(id);
        HashMap<String, String> map = new HashMap<>();

        if (comment.isEmpty()) {
            map.put("message", "Comment not found");
            throw new DomainException(HttpStatus.NOT_FOUND, map);
        }

        Smesharik authorPost = findPostAuthorByComment(comment.get());
        if (!authorPost.equals(commonService.getCurrentSmesharik()) &&
                !friendService.areFriends(authorPost.getLogin(),
                commonService.getCurrentSmesharik().getLogin()) ) {
            map.put("message", "Access denied!");
            throw new AccessDeniedException(map);
        }
        return domainMapper.toCommentWithChildrenResponse(comment.get());
    }

    public PaginatedResponse<CommentResponse> getAll(Long commentId, Long postId, @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size) {
        HashMap<String, String> map = new HashMap<>();
        Pair<Comment, Post> pair = commonService.getParentCommentOrPost(commentId, postId);
        Comment comment = pair.getLeft();
        Post post = pair.getRight();

        String authorPost = post != null ? post.getAuthor().getLogin() : findPostAuthorByComment(comment).getLogin();

        if (!authorPost.equals(commonService.getCurrentSmesharik().getLogin()) &&
                !friendService.areFriends(
                        authorPost,
                        commonService.getCurrentSmesharik().getLogin())
        ) {
            map.put("message", "Access denied!");
            throw new AccessDeniedException(map);
        }

        PageRequest pageable = PageRequest.of(page, size);

        Page<CommentWithChildrenDto> resultPage = commentRepository.findCommentsByPostOrParentComment(
                pair.getRight(), pair.getLeft(), pageable
        );

        List<CommentResponse> content = resultPage.getContent().stream()
                .map(domainMapper::toCommentWithChildrenResponse)
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                content,
                resultPage.getTotalPages(),
                resultPage.getTotalElements(),
                resultPage.getNumber(),
                resultPage.getSize()
        );
    }
}

