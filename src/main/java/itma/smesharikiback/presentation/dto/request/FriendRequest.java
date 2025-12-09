package itma.smesharikiback.presentation.dto.request;

import itma.smesharikiback.domain.model.FriendStatus;
import itma.smesharikiback.presentation.validation.Login;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class FriendRequest {

    @NotBlank(message = "Поле followee обязательно.")
    @Login(message = "Поле followee должно содержать корректный логин.")
    private String followee;

    @NotBlank(message = "Поле follower обязательно.")
    @Login(message = "Поле follower должно содержать корректный логин.")
    private String follower;

    private FriendStatus status = FriendStatus.NEW;
}
