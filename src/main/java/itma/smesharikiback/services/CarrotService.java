package itma.smesharikiback.services;

import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Carrot;
import itma.smesharikiback.models.Comment;
import itma.smesharikiback.models.Post;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.reposirories.CarrotRepository;
import itma.smesharikiback.response.CarrotResponse;
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

    public CarrotResponse create(Long post, Long comment) {
        Carrot carrot = new Carrot();
        Pair<Comment, Post> pair = commonService.getParentCommentOrPost(comment, post);
        Post parentPost = pair.getRight();
        Comment parentComment = pair.getLeft();

        Smesharik smesharik;
        if (post == null) smesharik = commentService.findPostAuthorByComment(parentComment);
        else smesharik = parentPost.getAuthor();

        if (!friendService.isFriendsOrAdmin(smesharik.getId(), commonService.getCurrentSmesharik().getId())) {
            HashMap<String, String> map = new HashMap<>();
            map.put("message", "Нельзя ставить лайки под постами не друзей.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, map);
        }

        carrot.setSmesharik(commonService.getCurrentSmesharik());
        carrot.setPost(parentPost);
        carrot.setComment(parentComment);

        return buildResponse(carrotRepository.save(carrot));
    }

    private CarrotResponse buildResponse(Carrot carrot) {
        CarrotResponse carrotResponse = new CarrotResponse();
        if (carrot.getComment() != null) carrotResponse.setComment(carrot.getComment().getId());
        if (carrot.getPost() != null) carrotResponse.setPost(carrot.getPost().getId());

        return carrotResponse
                .setId(carrot.getId())
                .setSmesharik(carrot.getSmesharik().getId());
    }

    public ResponseEntity<?> delete(Long post, Long comment) {
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
            map.put("message", "Carrot не найден.");
            throw new GeneralException(HttpStatus.NOT_FOUND, map);
        }

        carrotRepository.delete(carrot.get());

        HashMap<String, String> map = new HashMap<>();
        map.put("message", "Carrot удалён.");
        return ResponseEntity.ok().body(map);
    }
}
