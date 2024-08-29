package ra.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer WishListId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToMany
    @JoinTable(
            name = "wishList_product",
            joinColumns = @JoinColumn(name = "WishListId"),
            inverseJoinColumns = @JoinColumn(name = "productId")
    )
    private Set<Product> products;
}
