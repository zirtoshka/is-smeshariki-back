package itma.smesharikiback.presentation.dto.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CarrotResponse {
    private Long id;
    private String smesharik;
    private Long post;
    private Long comment;
    private LocalDateTime creationDate = LocalDateTime.now();
}













