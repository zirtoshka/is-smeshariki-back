package itma.smesharikiback.presentation.dto.request;

import itma.smesharikiback.domain.model.GeneralStatus;
import itma.smesharikiback.domain.model.ViolationType;
import itma.smesharikiback.presentation.validation.Login;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ComplaintRequest {

    private ViolationType violationType = ViolationType.SPAM;
    private String description;

    @Login(message = "Поле admin должно содержать корректный логин.")
    private String admin;

    @Positive(message = "Поле post должно быть положительным.")
    private Long post;

    @Positive(message = "Поле comment должно быть положительным.")
    private Long comment;

    private GeneralStatus status = GeneralStatus.NEW;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime closingDate;
}
