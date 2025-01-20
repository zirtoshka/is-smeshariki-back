package itma.smesharikiback.services;

import itma.smesharikiback.config.PaginationSpecification;
import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.SmesharikRole;
import itma.smesharikiback.models.dto.PostWithCarrotsDto;
import itma.smesharikiback.models.reposirories.CommentRepository;
import itma.smesharikiback.models.reposirories.PostRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CommonService {
    protected CommentRepository commentRepository;
    protected PostRepository postRepository;
    protected SmesharikService smesharikService;
    protected FriendService friendService;


    public Boolean isFriendsOrAdmin(Long authorId, Long userId) {
        return (authorId.equals(userId) ||
                smesharikService.getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN) ||
                friendService.areFriends(authorId, userId));
    }

    public Pair<Comment, Post> getParentCommentOrPost(Long commentId, Long postId) {
        HashMap<String, String> map = new HashMap<>();
        Optional<Post> postOpt = Optional.empty();
        Optional<Comment> commentOpt = Optional.empty();
        if (postId != null) postOpt = postRepository.findById(postId);
        if (commentId != null) commentOpt = commentRepository.findById(commentId);

        if ((commentOpt.isPresent() && postOpt.isPresent()) ) {
            map.put("message", "Должен быть выставлено 1 из 2 параметров: comment, post.");
            throw new GeneralException(HttpStatus.NOT_FOUND, map);
        }
        if ((commentOpt.isEmpty() && postOpt.isEmpty()) ) {
            map.put("message", "Не было найдено связанного post или comment.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, map);
        }
        Comment comment = commentOpt.orElse(null);
        Post post = postOpt.orElse(null);

        return Pair.of(comment, post);
    }
}
