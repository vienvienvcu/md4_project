package ra.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {
    private String serialNumber; // Phải UUID tự sinh ra, có thể được tạo trên server

    @NotBlank(message = "Receive name cannot be empty")
    private String receiveName;

    @NotBlank(message = "Receive address cannot be empty")
    private String receiveAddress;

    @NotBlank(message = "Receive phone cannot be empty")
    private String receivePhone;


}
