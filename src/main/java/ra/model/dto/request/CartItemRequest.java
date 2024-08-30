package ra.model.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartItemRequest {
        @NotNull(message = "Product cannot be empty")
        private Long productId;
        @NotNull(message = "Quantity cannot be empty")
        private Integer quantity;
}
