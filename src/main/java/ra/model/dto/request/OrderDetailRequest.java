package ra.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderDetailRequest {

    @NotBlank(message = "Product Name cannot be empty")
    private String productName;

    @NotNull(message = "Product price cannot be empty")
    private Double productPrice;

    @NotNull(message = "Product quantity cannot be empty")
    private Integer productQuantity;

    @NotNull(message = "Product id cannot be empty")
    private Long productId;

    @NotNull(message = "OrderId cannot be empty")
    private Long orderId;
}
