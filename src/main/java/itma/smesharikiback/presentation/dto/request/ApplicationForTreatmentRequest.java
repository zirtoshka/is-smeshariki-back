package itma.smesharikiback.presentation.dto.request;

import itma.smesharikiback.domain.model.GeneralStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApplicationForTreatmentRequest {
    private Long post;
    private Long comment;
    private String doctor;
    private GeneralStatus status = GeneralStatus.NEW;
}













