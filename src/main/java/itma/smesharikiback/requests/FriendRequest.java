package itma.smesharikiback.requests;

import itma.smesharikiback.models.FriendStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class FriendRequest {
    @Min(value = 0, message = "Значение поля y должно быть больше 0")
    private Long followee;

    @Min(value = 0, message = "Значение поля y должно быть больше 0")
    private Long follower;

    private FriendStatus status = FriendStatus.NEW;
}
