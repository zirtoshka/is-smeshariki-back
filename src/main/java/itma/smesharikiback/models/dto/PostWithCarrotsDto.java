package itma.smesharikiback.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostWithCarrotsDto {
    private Long id;
    private Long author;
    private Boolean isDraft;
    private String text;
    private Boolean isPrivate ;
    private LocalDateTime publicationDate;
    private String pathToImage;
    private LocalDateTime creationDate;
    private Long countCarrots;
}
