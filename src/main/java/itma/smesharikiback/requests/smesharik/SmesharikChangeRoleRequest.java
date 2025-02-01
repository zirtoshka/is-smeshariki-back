package itma.smesharikiback.requests.smesharik;

import itma.smesharikiback.models.SmesharikRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class SmesharikChangeRoleRequest {
    @NotNull(message = "role не может быть null.")
    private SmesharikRole role;
}
