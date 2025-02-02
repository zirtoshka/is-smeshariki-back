package itma.smesharikiback.requests;

import itma.smesharikiback.models.FriendStatus;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class FriendRequest {

    private String followee;

    private String follower;

    private FriendStatus status = FriendStatus.NEW;
}
