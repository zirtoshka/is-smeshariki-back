package itma.smesharikiback.presentation.dto.request.smesharik;

import itma.smesharikiback.domain.model.SmesharikRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class SmesharikChangeRoleRequest {
    @NotNull(message = "Поле role обязательно.")
    private SmesharikRole role;
}
