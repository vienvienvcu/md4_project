package ra.model.entity;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user; // Một giỏ hàng thuộc về một người dùng

    @ManyToMany
    @JoinTable(
            name = "cart_product",
            joinColumns = @JoinColumn(name = "cartId"),
            inverseJoinColumns = @JoinColumn(name = "productId")
    )
    private Set<Product> products;

    @NotBlank(message = "Quantity is cannot empty")
    private Integer quantity;
}
