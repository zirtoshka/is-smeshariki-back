package itma.smesharikiback.services;

import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.dto.CommentWithChildrenDto;
import itma.smesharikiback.models.reposirories.CommentRepository;
import itma.smesharikiback.models.reposirories.PostRepository;
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
    private final SmesharikService smesharikService;
    private final FriendService friendService;
    private final CommentRepository commentRepository;
    private final CommonService commonService;

    public CommentResponse create(CommentRequest request) throws GeneralException {
        Pair<Comment, Post> pair = commonService.getParentCommentOrPost(request.getParentComment(), request.getPost());

        Comment comment = new Comment();
        Post post = pair.getRight();
        Comment comment1 = pair.getLeft();

        Smesharik smesharik;
        if (post == null) smesharik = findPostAuthorByComment(comment1);
        else smesharik = post.getAuthor();

        if (!commonService.isFriendsOrAdmin(smesharik.getId(), smesharikService.getCurrentSmesharik().getId())) {}

        comment.setPost(post);
        comment.setParentComment(comment1);
        comment.setText(request.getText());
        comment.setCreationDate(new Timestamp(new Date().getTime()).toLocalDateTime());
        comment.setSmesharik(smesharikService.getCurrentSmesharik());

        return buildResponse(commentRepository.save(comment));
    }

    private Smesharik findPostAuthorByComment(Comment comment) throws GeneralException {
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
                .setSmesharik(comment.getSmesharik().getId())
                .setText(comment.getText());
    }

    private CommentWithChildrenResponse buildWithChildrenResponse(CommentWithChildrenDto comment) {
        CommentWithChildrenResponse response = new CommentWithChildrenResponse();
        if (comment.getParentComment() != null) response.setParentComment(comment.getParentComment());
        if (comment.getPost() != null) response.setPost(comment.getPost());
        response.setHasChildren(comment.isHasChildren());
        response.setCountCarrots(comment.getCountCarrots());

        return (CommentWithChildrenResponse) response
                .setId(comment.getId())
                .setCreationDate(comment.getCreationDate())
                .setSmesharik(comment.getSmesharik())
                .setText(comment.getText());
    }

    public CommentResponse get(Long id) {
        Optional<CommentWithChildrenDto> comment = commentRepository.findByIdWithChildren(id);
        if (comment.isEmpty()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "Комментарий не был найден");
            throw new GeneralException(HttpStatus.NOT_FOUND, map);
        }
        return buildWithChildrenResponse(comment.get());
    }

    public PaginatedResponse<CommentResponse> getAll(Long commentId, Long postId, @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size) {
        Pair<Comment, Post> pair = commonService.getParentCommentOrPost(commentId, postId);

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
