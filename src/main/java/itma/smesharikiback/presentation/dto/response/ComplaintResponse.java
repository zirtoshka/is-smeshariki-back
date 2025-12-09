package itma.smesharikiback.presentation.dto.response;

import itma.smesharikiback.domain.model.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ComplaintResponse {
    private Long id;
    private ViolationType violationType;
    private String description;
    private String admin;
    private Long post;
    private Long comment;
    private GeneralStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime closingDate;
}













