package itma.smesharikiback.requests;

import itma.smesharikiback.models.GeneralStatus;
import itma.smesharikiback.models.ViolationType;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ComplaintRequest {

    private ViolationType violationType = ViolationType.SPAM;
    private String description;

    @Min(value = 0, message = "Значение поля admin должно быть больше 0")
    private Long admin;
    @Min(value = 0, message = "Значение поля post должно быть больше 0")
    private Long post;
    @Min(value = 0, message = "Значение поля comment должно быть больше 0")
    private Long comment;

    private GeneralStatus status = GeneralStatus.NEW;
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime closingDate;
}

