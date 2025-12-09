package itma.smesharikiback.presentation.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TriggerWordResponse {
    private Long id;
    private String word;
    private Long propensity;
}













