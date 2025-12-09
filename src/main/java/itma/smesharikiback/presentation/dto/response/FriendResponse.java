package itma.smesharikiback.presentation.dto.response;

import itma.smesharikiback.domain.model.FriendStatus;
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













