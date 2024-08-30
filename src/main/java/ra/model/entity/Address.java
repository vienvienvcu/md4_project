package ra.model.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;




@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer addressId;

    @NotBlank(message = "Full address in cannot empty")
  private String fullAddress;

    @NotBlank(message = "Full address in cannot empty")
  private String receiveName;

    @NotBlank(message = "Full address in cannot empty")
  private String phone;


    @ManyToOne
    @JoinColumn(name = "userId")
    private Users user;

}
