package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import ra.constans.OrderStatus;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    private String serialNumber;
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private String receiveName;
    private String receiveAddress;
    private String receivePhone;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createTime;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date receivedTime;


    @ManyToOne
    @JoinColumn(name = "userId")
    private Users users;
}
