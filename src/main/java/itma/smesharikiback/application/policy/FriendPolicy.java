package itma.smesharikiback.application.policy;

import itma.smesharikiback.application.service.CommonService;
import itma.smesharikiback.domain.exception.AccessDeniedException;
import itma.smesharikiback.domain.exception.ValidationException;
import itma.smesharikiback.domain.model.Friend;
import itma.smesharikiback.domain.model.FriendStatus;
import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.model.SmesharikRole;
import itma.smesharikiback.domain.repository.FriendRepository;
import itma.smesharikiback.domain.repository.SmesharikRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FriendPolicy {
    private final FriendRepository friendRepository;
    private final SmesharikRepository smesharikRepository;
    private final CommonService commonService;

    public Pair<Smesharik, Smesharik> requireParticipants(String followeeLogin, String followerLogin) {
        Smesharik followee = smesharikRepository.findByLogin(followeeLogin).orElse(null);
        Smesharik follower = smesharikRepository.findByLogin(followerLogin).orElse(null);
        return validateParticipants(followee, follower);
    }

    public Pair<Smesharik, Smesharik> requireParticipantsById(Long followeeId, Long followerId) {
        Smesharik followee = smesharikRepository.findById(followeeId).orElse(null);
        Smesharik follower = smesharikRepository.findById(followerId).orElse(null);
        return validateParticipants(followee, follower);
    }

    private Pair<Smesharik, Smesharik> validateParticipants(Smesharik followee, Smesharik follower) {
        Map<String, String> errors = new HashMap<>();
        if (followee == null) {
            errors.put("followee", "Пользователь followee не найден.");
        }
        if (follower == null) {
            errors.put("follower", "Пользователь follower не найден.");
        }
        if (followee != null && followee.equals(follower)) {
            errors.put("followee", "Нельзя добавить в друзья самого себя.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        return Pair.of(followee, follower);
    }

    public void ensureNoExistingRequest(Smesharik followee, Smesharik follower) {
        if (areFriendsRequestExist(followee, follower)) {
            Map<String, String> errors = new HashMap<>();
            errors.put("message", "Заявка в друзья уже существует.");
            throw new ValidationException(errors);
        }
    }

    public Friend requireFriendship(String followeeLogin, String followerLogin) {
        Pair<Smesharik, Smesharik> pair = requireParticipants(followeeLogin, followerLogin);
        return requireFriendship(pair.getLeft(), pair.getRight());
    }

    public Friend requireFriendship(Smesharik followee, Smesharik follower) {
        Optional<Friend> existingRequest = friendRepository.findByFollowerAndFollowee(follower, followee);
        if (existingRequest.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            errors.put("message", "Заявка в друзья не найдена.");
            throw new ValidationException(errors);
        }
        return existingRequest.get();
    }

    public void assertCurrentUserIsFollower(Smesharik follower) {
        if (!follower.equals(commonService.getCurrentSmesharik())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("message", "Нельзя отправлять или отзывать заявку от имени другого пользователя.");
            throw new AccessDeniedException(errors);
        }
    }

    public void assertCurrentUserIsFollowee(Friend friend) {
        if (!friend.getFollowee().equals(commonService.getCurrentSmesharik())) {
            Map<String, String> errors = new HashMap<>();
            errors.put("message", "Подтвердить заявку может только адресат.");
            throw new AccessDeniedException(errors);
        }
    }

    public void assertPending(Friend friend) {
        if (friend.getStatus() != FriendStatus.NEW) {
            Map<String, String> errors = new HashMap<>();
            errors.put("message", "Заявка уже обработана.");
            throw new ValidationException(errors);
        }
    }

    public boolean isFriendsOrAdmin(Smesharik author, Smesharik actor) {
        return author.equals(actor) ||
                actor.getRole() == SmesharikRole.ADMIN ||
                areFriends(author, actor);
    }

    public boolean areFriends(Smesharik followee, Smesharik follower) {
        Friend friends1 = friendRepository.findByFollowerAndFollowee(follower, followee).orElse(null);
        Friend friends2 = friendRepository.findByFollowerAndFollowee(followee, follower).orElse(null);
        if (friends1 != null) {
            return friends1.getStatus() == FriendStatus.FRIENDS;
        } else if (friends2 != null) {
            return friends2.getStatus() == FriendStatus.FRIENDS;
        }
        return false;
    }

    public boolean areFriendsById(Long authorId, Long id) {
        Pair<Smesharik, Smesharik> pair = requireParticipantsById(authorId, id);
        return areFriends(pair.getLeft(), pair.getRight());
    }

    private boolean areFriendsRequestExist(Smesharik followee, Smesharik follower) {
        boolean isExist1 = friendRepository.existsByFollowerAndFollowee(follower, followee);
        boolean isExist2 = friendRepository.existsByFollowerAndFollowee(followee, follower);
        return isExist1 || isExist2;
    }
}
