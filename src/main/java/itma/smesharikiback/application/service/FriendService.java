package itma.smesharikiback.application.service;

import itma.smesharikiback.application.mapper.DomainMapper;
import itma.smesharikiback.application.policy.FriendPolicy;
import itma.smesharikiback.domain.model.Friend;
import itma.smesharikiback.domain.model.FriendStatus;
import itma.smesharikiback.domain.model.Smesharik;
import itma.smesharikiback.domain.repository.FriendRepository;
import itma.smesharikiback.infrastructure.specification.FriendSpecification;
import itma.smesharikiback.presentation.dto.request.FriendRequest;
import itma.smesharikiback.presentation.dto.response.FriendResponse;
import itma.smesharikiback.presentation.dto.response.MessageResponse;
import itma.smesharikiback.presentation.dto.response.PaginatedResponse;
import itma.smesharikiback.presentation.dto.response.SmesharikResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final CommonService commonService;
    private final FriendPolicy friendPolicy;
    private final DomainMapper domainMapper;

    @Transactional
    public FriendResponse create(FriendRequest friendRequest) {
        Pair<Smesharik, Smesharik> pair = friendPolicy.requireParticipants(
                friendRequest.getFollowee(),
                friendRequest.getFollower()
        );
        Smesharik followee = pair.getLeft();
        Smesharik follower = pair.getRight();

        friendPolicy.ensureNoExistingRequest(followee, follower);
        friendPolicy.assertCurrentUserIsFollower(follower);

        Friend friend = new Friend();
        friend.setFollowee(followee);
        friend.setFollower(follower);
        friend.setStatus(friendRequest.getStatus());
        return domainMapper.toFriendResponse(friendRepository.save(friend));
    }

    @Transactional
    public FriendResponse acceptFriendRequest(String followerId, String followeeId) {
        Friend friendRequest = friendPolicy.requireFriendship(followeeId, followerId);
        friendPolicy.assertPending(friendRequest);
        friendPolicy.assertCurrentUserIsFollowee(friendRequest);

        friendRequest.setStatus(FriendStatus.FRIENDS);
        return domainMapper.toFriendResponse(friendRepository.save(friendRequest));
    }

    @Transactional
    public MessageResponse removeFriend(String followerId, String followeeId) {
        Friend friend = friendPolicy.requireFriendship(followeeId, followerId);
        friendRepository.delete(friend);
        return new MessageResponse().setMessage("Дружба удалена.");
    }

    @Transactional
    public PaginatedResponse<SmesharikResponse> getFriends(
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

    public PaginatedResponse<SmesharikResponse> getFollowers(
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

    public PaginatedResponse<SmesharikResponse> getFollows(
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

    public PaginatedResponse<SmesharikResponse> getPaginatedFriends(
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

        Smesharik currentSmesharik = commonService.getCurrentSmesharik();

        List<SmesharikResponse> content = resultPage.getContent().stream()
                .map(friend -> {
                    Smesharik smesharik = friend.getFollower().equals(currentSmesharik)
                            ? friend.getFollowee()
                            : friend.getFollower();
                    return domainMapper.toSmesharikResponse(smesharik);
                })
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
