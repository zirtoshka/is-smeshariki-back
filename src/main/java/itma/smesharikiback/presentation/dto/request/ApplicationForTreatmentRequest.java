package itma.smesharikiback.presentation.dto.request;

import itma.smesharikiback.domain.model.GeneralStatus;
import itma.smesharikiback.presentation.validation.Login;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApplicationForTreatmentRequest {
    @Positive(message = "Поле post должно быть положительным.")
    private Long post;

    @Positive(message = "Поле comment должно быть положительным.")
    private Long comment;

    @Login(message = "Поле doctor должно быть корректным логином.")
    private String doctor;
    private GeneralStatus status = GeneralStatus.NEW;
}
