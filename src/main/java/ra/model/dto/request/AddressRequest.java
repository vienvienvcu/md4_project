package ra.model.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AddressRequest {
    @NotBlank(message = "Full address in cannot empty")
    private String fullAddress;
    @NotBlank(message = "Full address in cannot empty")
    private String receiveName;
    @NotBlank(message = "Full address in cannot empty")

    private String phone;
    private Long userId;
}
