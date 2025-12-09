package itma.smesharikiback.presentation.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostWithCarrotsResponse extends PostResponse {
    protected Long countCarrots;
}













