package itma.smesharikiback.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BanRequest {

    @NotNull(message = "reason не может быть null")
    @NotBlank(message = "reason не может быть пустой")
    private String reason;

    private String smesharik;
    private Long post;
    private Long comment;

    private LocalDateTime endDate = LocalDateTime.now().plusHours(1);

    private LocalDateTime creationDate = LocalDateTime.now();
}













