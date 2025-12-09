package itma.smesharikiback.application.service;

import itma.smesharikiback.application.mapper.DomainMapper;
import itma.smesharikiback.domain.exception.DomainException;
import itma.smesharikiback.domain.exception.ValidationException;
import itma.smesharikiback.domain.model.Carrot;
import itma.smesharikiback.domain.model.Comment;
import itma.smesharikiback.domain.model.Post;
import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.repository.CarrotRepository;
import itma.smesharikiback.presentation.dto.response.CarrotResponse;
import itma.smesharikiback.presentation.dto.response.MessageResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CarrotService {
    private final CarrotRepository carrotRepository;
    private final CommonService commonService;
    private final CommentService commentService;
    private final FriendService friendService;
    private final DomainMapper domainMapper;

    public CarrotResponse create(Long post, Long comment) {
        Carrot carrot = new Carrot();
        Pair<Comment, Post> pair = commonService.getParentCommentOrPost(comment, post);
        Post parentPost = pair.getRight();
        Comment parentComment = pair.getLeft();

        Smesharik smesharik;
        if (post == null) {
            smesharik = commentService.findPostAuthorByComment(parentComment);
        } else {
            smesharik = parentPost.getAuthor();
        }

        if (!friendService.isFriendsOrAdmin(smesharik.getLogin(), commonService.getCurrentSmesharik().getLogin())) {
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "Carrot can be added only for friends or by admin.");
            throw new ValidationException(map);
        }

        carrot.setSmesharik(commonService.getCurrentSmesharik());
        carrot.setPost(parentPost);
        carrot.setComment(parentComment);

        return domainMapper.toCarrotResponse(carrotRepository.save(carrot));
    }

    public ResponseEntity<?> delete(Long post, Long comment) {
        Carrot carrot = getCarrot(comment, post);

        carrotRepository.delete(carrot);

        HashMap<String, String> map = new HashMap<>();
        map.put("message", "Carrot deleted.");
        return ResponseEntity.ok().body(map);
    }

    public MessageResponse check(Long post, Long comment) {
        getCarrot(comment, post);
        return new MessageResponse().setMessage("Carrot already exists.");
    }


    private Carrot getCarrot(Long comment, Long post) {
        Pair<Comment, Post> pair = commonService.getParentCommentOrPost(comment, post);
        Post parentPost = pair.getRight();
        Comment parentComment = pair.getLeft();

        Optional<Carrot> carrot = Optional.empty();
        if (parentComment != null) {
            carrot = carrotRepository.findBySmesharikAndComment(
                    commonService.getCurrentSmesharik(), parentComment
            );
        } else if (parentPost != null) {
            carrot = carrotRepository.findBySmesharikAndPost(
                    commonService.getCurrentSmesharik(), parentPost
            );
        }
        if (carrot.isEmpty()) {
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "Carrot not found.");
            throw new DomainException(HttpStatus.NOT_FOUND, map);
        }

        return carrot.get();
    }
}

