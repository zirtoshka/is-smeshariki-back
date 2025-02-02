package itma.smesharikiback.services;

import itma.smesharikiback.exceptions.GeneralException;
import itma.smesharikiback.models.Friend;
import itma.smesharikiback.models.FriendStatus;
import itma.smesharikiback.models.Smesharik;
import itma.smesharikiback.models.SmesharikRole;
import itma.smesharikiback.models.reposirories.FriendRepository;
import itma.smesharikiback.models.reposirories.SmesharikRepository;
import itma.smesharikiback.requests.FriendRequest;
import itma.smesharikiback.response.FriendResponse;
import itma.smesharikiback.response.MessageResponse;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.specification.FriendSpecification;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
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
    private final CommonService commonService;

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
        } else if (!follower.equals(commonService.getCurrentSmesharik())){
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
    public FriendResponse acceptFriendRequest(String followerId, String followeeId) {
        Friend friendRequest = getFriendship(followeeId, followerId);

        HashMap<String, String> errors = new HashMap<>();
        if (friendRequest.getStatus() != FriendStatus.NEW) {
            errors.put("message", "Такая заявка была обработана.");
            throw new GeneralException(HttpStatus.BAD_REQUEST, errors);
        }
        if (friendRequest.getFollowee() != commonService.getCurrentSmesharik()) {
            errors.put("message", "Ошибка доступа.");
            throw new GeneralException(HttpStatus.FORBIDDEN, errors);
        }

        friendRequest.setStatus(FriendStatus.FRIENDS);
        return buildResponse(friendRepository.save(friendRequest));
    }

    @Transactional
    public MessageResponse removeFriend(String followerId, String followeeId) {
        Friend friend = getFriendship(followeeId, followerId);
        friendRepository.delete(friend);
        return new MessageResponse().setMessage("Друг удален.");
    }


    public Pair<Smesharik, Smesharik> getSmeshariks(String followeeLogin, String followerLogin) {
        Smesharik followee = smesharikRepository.findByLogin(followeeLogin).orElse(null);
        Smesharik follower = smesharikRepository.findByLogin(followerLogin).orElse(null);

        return getSmesharikPair(followee, follower);

    }

    public Pair<Smesharik, Smesharik> getSmeshariksById(Long followeeId, Long followerId) {
        Smesharik followee = smesharikRepository.findById(followeeId).orElse(null);
        Smesharik follower = smesharikRepository.findById(followerId).orElse(null);

        return getSmesharikPair(followee, follower);
    }

    @NotNull
    private Pair<Smesharik, Smesharik> getSmesharikPair(Smesharik followee, Smesharik follower) {
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

    public Friend getFriendship(String followeeLogin, String followerLogin) {
        Pair<Smesharik, Smesharik> pair = getSmeshariks(
                followeeLogin,
                followerLogin
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

    public Boolean isFriendsOrAdmin(String authorLogin, String userLogin) {
        return (authorLogin.equals(userLogin) ||
                commonService.getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN) ||
                areFriends(authorLogin, userLogin));
    }

    public boolean areFriends(String followeeLogin, String followerLogin) {
        Pair<Smesharik, Smesharik> pair = getSmeshariks(
                followeeLogin,
                followerLogin
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
                .setFollowee(friend.getFollowee().getLogin())
                .setFollower(friend.getFollower().getLogin())
                .setStatus(friend.getStatus());
    }


    @Transactional
    public PaginatedResponse<FriendResponse> getFriends(
            String nameOrLogin,
            @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size
    ) {
        Smesharik followee = commonService.getCurrentSmesharik();
        return getPaginatedFriends(
                nameOrLogin,
                page,
                size,
                FriendStatus.FRIENDS,
                followee,
                followee
        );
    }

    public PaginatedResponse<FriendResponse> getFollowers(
            String nameOrLogin,
            @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size
    ) {
        Smesharik followee = commonService.getCurrentSmesharik();

        return getPaginatedFriends(
                nameOrLogin,
                page,
                size,
                FriendStatus.NEW,
                followee,
                null
        );
    }

    public PaginatedResponse<FriendResponse> getFollows(
            String nameOrLogin,
            @Min(value = 0) Integer page, @Min(value = 1) @Max(value = 50) Integer size
    ) {
        Smesharik follower = commonService.getCurrentSmesharik();

        return getPaginatedFriends(
                nameOrLogin,
                page,
                size,
                FriendStatus.NEW,
                null,
                follower
        );
    }

    public PaginatedResponse<FriendResponse> getPaginatedFriends(
            String nameOrLogin, Integer page, Integer size, FriendStatus friendStatus, Smesharik followee, Smesharik follower
    ) {
        PageRequest pageRequest = PageRequest.of(
                page,
                size
        );

        Page<Friend> resultPage = friendRepository.findAll(
                FriendSpecification.hasNameOrLogin(nameOrLogin)
                        .and(FriendSpecification.hasStatusaAndId(friendStatus, followee, follower)),
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

    public boolean isFriendsOrAdminId(Long author, Long id) {
        return (author.equals(id) ||
                commonService.getCurrentSmesharik().getRole().equals(SmesharikRole.ADMIN) ||
                areFriendsId(author, id));
    }

    private boolean areFriendsId(Long author, Long id) {
        Pair<Smesharik, Smesharik> pair = getSmeshariksById(
                author,
                id
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
}
