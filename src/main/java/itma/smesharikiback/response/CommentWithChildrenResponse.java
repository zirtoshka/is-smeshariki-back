package itma.smesharikiback.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentWithChildrenResponse extends CommentResponse {
    protected Boolean hasChildren;
}
