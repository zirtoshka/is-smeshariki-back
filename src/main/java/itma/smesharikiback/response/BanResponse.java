package itma.smesharikiback.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BanResponse {
    private Long id;
    private String reason;
    private String smesharik;
    private Long post;
    private LocalDateTime endDate;
    private Long comment;
    private LocalDateTime creationDate;
}
