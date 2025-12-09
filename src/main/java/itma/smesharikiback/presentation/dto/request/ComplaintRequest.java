package itma.smesharikiback.presentation.dto.request;

import itma.smesharikiback.domain.model.GeneralStatus;
import itma.smesharikiback.domain.model.ViolationType;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ComplaintRequest {

    private ViolationType violationType = ViolationType.SPAM;
    private String description;

    private String admin;
    @Min(value = 0, message = "Значение поля post должно быть больше 0")
    private Long post;
    @Min(value = 0, message = "Значение поля comment должно быть больше 0")
    private Long comment;

    private GeneralStatus status = GeneralStatus.NEW;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime closingDate;
}














