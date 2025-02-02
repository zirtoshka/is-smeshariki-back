package itma.smesharikiback.response;

import itma.smesharikiback.models.GeneralStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class  ApplicationForTreatmentResponse {
    private Long id;
    private Long post;
    private Long comment;
    private GeneralStatus status;
    private String doctor;
    private List<Long> propensities;
}

