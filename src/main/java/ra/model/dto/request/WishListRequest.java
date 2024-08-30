package ra.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WishListRequest {
    @NotNull(message = "Product cannot be empty")
    private Long productId;
}
