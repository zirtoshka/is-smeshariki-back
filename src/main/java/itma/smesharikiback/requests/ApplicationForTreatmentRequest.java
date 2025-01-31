package itma.smesharikiback.requests;

import itma.smesharikiback.models.GeneralStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ApplicationForTreatmentRequest {
    private Long post;
    private Long comment;
    private GeneralStatus status = GeneralStatus.NEW;
}
