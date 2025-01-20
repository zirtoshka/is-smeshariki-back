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
            @RequestParam @Min(value = 0, message = "follower не может быть отрицательным") @Validated Long follower,
            @RequestParam @Min(value = 0, message = "followee не может быть отрицательным") @Validated Long followee
            ) {
        return friendService.acceptFriendRequest(follower, followee);
    }

    @DeleteMapping
    public MessageResponse deleteFriendship(
            @RequestParam @Min(value = 0, message = "follower не может быть отрицательным") @Validated Long follower,
            @RequestParam @Min(value = 0, message = "followee не может быть отрицательным") @Validated Long followee
    ) {
        return friendService.removeFriend(follower, followee);
    }

    @GetMapping
    public PaginatedResponse<FriendResponse> getFriends(
            @RequestParam Long followee,
            @RequestParam(required = false, defaultValue = "0") @Min(value = 0) Integer page,
            @RequestParam(required = false, defaultValue = "10") @Min(value = 1) @Max(value = 50) Integer size
    ) {
        return friendService.getFriends(followee, page, size);
    }
}
