package itma.smesharikiback.controllers;


import itma.smesharikiback.requests.FriendRequest;
import itma.smesharikiback.response.FriendResponse;
import itma.smesharikiback.response.MessageResponse;
import itma.smesharikiback.response.PaginatedResponse;
import itma.smesharikiback.services.FriendService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/friend")
@AllArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public FriendResponse addFriend(@Validated @RequestBody FriendRequest friendRequest) {
        return friendService.create(friendRequest);
    }

    @PostMapping("/acceptFriend")
    public FriendResponse acceptFriend(
            @RequestParam @Validated String follower,
            @RequestParam @Validated String followee
            ) {
        return friendService.acceptFriendRequest(follower, followee);
    }

    @DeleteMapping
    public MessageResponse deleteFriendship(
            @RequestParam @Validated String follower,
            @RequestParam @Validated String followee
    ) {
        return friendService.removeFriend(follower, followee);
    }

    @GetMapping
    public PaginatedResponse<FriendResponse> getFriends(
            @RequestParam(required = false, defaultValue = "") String nameOrLogin,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return friendService.getFriends(nameOrLogin, page, size);
    }

    @GetMapping("/followers")
    public PaginatedResponse<FriendResponse> getFollowers(
            @RequestParam(required = false, defaultValue = "") String nameOrLogin,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return friendService.getFollowers(nameOrLogin, page, size);
    }

    @GetMapping("/follows")
    public PaginatedResponse<FriendResponse> getFollows(
            @RequestParam(required = false, defaultValue = "") String nameOrLogin,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return friendService.getFollows(nameOrLogin, page, size);
    }
}
