package ra.model.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ra.constans.OrderStatus;

import java.util.Date;


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

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createTime;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date receivedTime;


    private OrderStatus orderStatus;


}
