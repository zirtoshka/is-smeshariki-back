package itma.smesharikiback.services;

import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Friend;
import itma.smesharikiback.models.FriendStatus;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.reposirories.FriendRepository;
import itma.smesharikiback.models.reposirories.SmesharikRepository;
import itma.smesharikiback.requests.FriendRequest;
import itma.smesharikiback.response.FriendResponse;
import itma.smesharikiback.response.MessageResponse;
import itma.smesharikiback.response.PaginatedResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final SmesharikRepository smesharikRepository;
    private final SmesharikService smesharikService;

    @Transactional
    public FriendResponse create(FriendRequest friendRequest) {
        Pair<Smesharik, Smesharik> pair = getSmeshariks(
                friendRequest.getFollowee(),
                friendRequest.getFollower()
        );
        Smesharik followee = pair.getLeft();
        Smesharik follower = pair.getRight();

        HashMap<String, String> errors = new HashMap<>();
        if (areFriendsRequestExist(followee, follower)) {
            errors.put("message", "Такая заявка в друзья уже существует.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        } else if (!follower.equals(smesharikService.getCurrentSmesharik())){
            errors.put("message", "Ошибка доступа.");
            throw new GeneralException(HttpStatus.FORBIDDEN, errors);
        }

        Friend friend = new Friend();
        friend.setFollowee(followee);
        friend.setFollower(follower);
        friend.setStatus(friendRequest.getStatus());
        return buildResponse(friendRepository.save(friend));
    }

    @Transactional
    public FriendResponse acceptFriendRequest(Long followerId, Long followeeId) {
        Friend friendRequest = getFriendship(followeeId, followerId);

        HashMap<String, String> errors = new HashMap<>();
        if (friendRequest.getStatus() != FriendStatus.NEW) {
            errors.put("message", "Такая заявка была обработана.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }
        if (friendRequest.getFollowee() != smesharikService.getCurrentSmesharik()) {
            errors.put("message", "Ошибка доступа.");
            throw new GeneralException(HttpStatus.FORBIDDEN, errors);
        }

        friendRequest.setStatus(FriendStatus.FRIENDS);
        return buildResponse(friendRepository.save(friendRequest));
    }

    @Transactional
    public MessageResponse removeFriend(Long followerId, Long followeeId) {
        Friend friend = getFriendship(followeeId, followerId);
        friendRepository.delete(friend);
        return new MessageResponse().setMessage("Друг удален.");
    }


    public Pair<Smesharik, Smesharik> getSmeshariks(Long followeeId, Long followerId) {
        Smesharik followee = smesharikRepository.findById(followeeId).orElse(null);
        Smesharik follower = smesharikRepository.findById(followerId).orElse(null);

        HashMap<String, String> errors = new HashMap<>();
        if (followee == null) {
            errors.put("followee", "Followee не был найден.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        } else if (follower == null) {
            errors.put("follower", "Follower не был найден.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        } else if (follower.equals(followee)) {
            errors.put("followee", "Followee не может быть равен follower.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }

        return Pair.of(followee, follower);
    }

    public Friend getFriendship(Long followeeId, Long followerId) {
        Pair<Smesharik, Smesharik> pair = getSmeshariks(
                followeeId,
                followerId
        );
        Smesharik followee = pair.getLeft();
        Smesharik follower = pair.getRight();

        Optional<Friend> existingRequest = friendRepository.findByFollowerAndFollowee(follower, followee);

        HashMap<String, String> errors = new HashMap<>();
        if (existingRequest.isEmpty()) {
            errors.put("message", "Такой заявки в друзья не существует.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }

        return existingRequest.get();
    }

    public boolean areFriendsRequestExist(Smesharik followee, Smesharik follower) {
        boolean isExist1 = friendRepository.existsByFollowerAndFollowee(follower, followee);
        boolean isExist2 = friendRepository.existsByFollowerAndFollowee(followee, follower);
        return isExist1 || isExist2;
    }

    public boolean areFriends(Long followeeId, Long followerId) {
        Pair<Smesharik, Smesharik> pair = getSmeshariks(
                followeeId,
                followerId
        );
        Smesharik followee = pair.getLeft();
        Smesharik follower = pair.getRight();

        Friend friends1 = friendRepository.findByFollowerAndFollowee(follower, followee).orElse(null);
        Friend friends2 = friendRepository.findByFollowerAndFollowee(followee, follower).orElse(null);
        if (friends1 != null) {
            return friends1.getStatus() == FriendStatus.FRIENDS;
        } else if (friends2 != null) {
            return friends2.getStatus() == FriendStatus.FRIENDS;
        }

        return false;
    }

    public FriendResponse buildResponse(Friend friend) {
        return new FriendResponse()
                .setId(friend.getId())
                .setFollowee(friend.getFollowee().getId())
                .setFollower(friend.getFollower().getId())
                .setStatus(friend.getStatus());
    }


    @Transactional
    public PaginatedResponse<FriendResponse> getFriends(
            @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size
    ) {
        Smesharik followee = smesharikService.getCurrentSmesharik();
        return getPaginatedFriends(
                page,
                size,
                FriendStatus.FRIENDS,
                followee,
                followee
        );
    }

    public PaginatedResponse<FriendResponse> getFollowers(
            @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size
    ) {
        Smesharik followee = smesharikService.getCurrentSmesharik();

        return getPaginatedFriends(
                page,
                size,
                FriendStatus.NEW,
                followee,
                null
        );
    }

    public PaginatedResponse<FriendResponse> getFollows(
            @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size
    ) {
        Smesharik follower = smesharikService.getCurrentSmesharik();

        return getPaginatedFriends(
                page,
                size,
                FriendStatus.NEW,
                null,
                follower
        );
    }

    public PaginatedResponse<FriendResponse> getPaginatedFriends(
            Integer page, Integer size, FriendStatus friendStatus, Smesharik followee, Smesharik follower
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size
        );

        Page<Friend> resultPage = friendRepository.findByFolloweeOrFollowerAndStatus(
                followee,
                follower,
                friendStatus,
                pageRequest);

        List<FriendResponse> content = resultPage.getContent().stream()
                .map(this::buildResponse)
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
