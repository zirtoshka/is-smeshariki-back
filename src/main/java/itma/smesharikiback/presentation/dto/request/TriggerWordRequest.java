package itma.smesharikiback.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TriggerWordRequest {

    @NotNull(message = "Поле word обязательно.")
    @NotEmpty(message = "Поле word не может быть пустым.")
    private String word;

    @NotNull(message = "Поле propensity обязательно.")
    @Positive(message = "Поле propensity должно быть положительным.")
    private Long propensity;
}
