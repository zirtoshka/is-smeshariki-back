package itma.smesharikiback.response;

import itma.smesharikiback.models.FriendStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FriendResponse {
    private Long id;
    private String followee;
    private String follower;
    private FriendStatus status;
}
