package itma.smesharikiback.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PropensityRequest {
    @NotNull(message = "Поле name обязательно.")
    @NotEmpty(message = "Поле name не может быть пустым.")
    private String name;
    private String description;
}
