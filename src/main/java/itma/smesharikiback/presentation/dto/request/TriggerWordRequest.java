package itma.smesharikiback.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TriggerWordRequest {

    @NotNull(message = "word не может быть null.")
    @NotEmpty(message = "word не может быть пустым.")
    private String word;

    @NotNull(message = "propensity не может быть null.")
    private Long propensity;
}













