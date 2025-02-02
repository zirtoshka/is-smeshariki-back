package itma.smesharikiback.services;

import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.dto.CommentWithChildrenDto;
import itma.smesharikiback.models.reposirories.CommentRepository;
import itma.smesharikiback.requests.CommentRequest;
import itma.smesharikiback.response.CommentResponse;
import itma.smesharikiback.response.CommentWithChildrenResponse;
import itma.smesharikiback.response.PaginatedResponse;
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

    public CommentResponse create(CommentRequest request) throws GeneralException {
        Pair<Comment, Post> pair = commonService.getParentCommentOrPost(request.getParentComment(), request.getPost());

        Comment comment = new Comment();
        Post post = pair.getRight();
        Comment comment1 = pair.getLeft();

        Smesharik smesharik;
        if (post == null) smesharik = findPostAuthorByComment(comment1);
        else smesharik = post.getAuthor();

        if (!friendService.isFriendsOrAdmin(smesharik.getLogin(), commonService.getCurrentSmesharik().getLogin()) ||
                (post != null && (post.getIsDraft() || post.getIsPrivate()))) {
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "Нельзя написать коммент здесь.");
            throw new GeneralException(HttpStatus.NOT_FOUND, map);
        }

        comment.setPost(post);
        comment.setParentComment(comment1);
        comment.setText(request.getText());
        comment.setCreationDate(new Timestamp(new Date().getTime()).toLocalDateTime());
        comment.setSmesharik(commonService.getCurrentSmesharik());
        CommentResponse commentResponse = buildResponse(commentRepository.save(comment));
        psychoService.addToCommentQueue(comment);

        return commentResponse;
    }

    public Smesharik findPostAuthorByComment(Comment comment) throws GeneralException {
        if (!Objects.isNull(comment.getParentComment())) {
            return findPostAuthorByComment(comment.getParentComment());
        }
        return comment.getPost().getAuthor();
    }

    public Smesharik findPostAuthorByComment(CommentWithChildrenDto comment) throws GeneralException {
        if (!Objects.isNull(comment.getParentComment())) {
            return findPostAuthorByComment(comment.getParentComment());
        }
        return comment.getPost().getAuthor();
    }

    private CommentResponse buildResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        if (comment.getParentComment() != null) response.setParentComment(comment.getParentComment().getId());
        if (comment.getPost() != null) response.setPost(comment.getPost().getId());

        return response
                .setId(comment.getId())
                .setCreationDate(comment.getCreationDate())
                .setSmesharik(comment.getSmesharik().getLogin())
                .setText(comment.getText());
    }

    private CommentWithChildrenResponse buildWithChildrenResponse(CommentWithChildrenDto comment) {
        CommentWithChildrenResponse response = new CommentWithChildrenResponse();
        if (comment.getParentComment() != null) response.setParentComment(comment.getParentComment().getId());
        if (comment.getPost() != null) response.setPost(comment.getPost().getId());
        response.setHasChildren(comment.isHasChildren());
        response.setCountCarrots(comment.getCountCarrots());

        return (CommentWithChildrenResponse) response
                .setId(comment.getId())
                .setCreationDate(comment.getCreationDate())
                .setSmesharik(comment.getSmesharik().getLogin())
                .setText(comment.getText());
    }

    public CommentResponse get(Long id) {
        Optional<CommentWithChildrenDto> comment = commentRepository.findByIdWithChildren(id);
        HashMap<String, String> map = new HashMap<>();

        if (comment.isEmpty()) {
            map.put("message", "Комментарий не был найден");
            throw new GeneralException(HttpStatus.NOT_FOUND, map);
        }

        Smesharik authorPost = findPostAuthorByComment(comment.get());
        if (!authorPost.equals(commonService.getCurrentSmesharik()) &&
                !friendService.areFriends(authorPost.getLogin(),
                commonService.getCurrentSmesharik().getLogin()) ) {
            map.put("message", "Ошибка доступа!");
            throw new GeneralException(HttpStatus.FORBIDDEN, map);
        }
        return buildWithChildrenResponse(comment.get());
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
            map.put("message", "Ошибка доступа!");
            throw new GeneralException(HttpStatus.FORBIDDEN, map);
        }

        PageRequest pageable = PageRequest.of(page, size);

        Page<CommentWithChildrenDto> resultPage = commentRepository.findCommentsByPostOrParentComment(
                pair.getRight(), pair.getLeft(), pageable
        );

        List<CommentResponse> content = resultPage.getContent().stream()
                .map(this::buildWithChildrenResponse)
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
