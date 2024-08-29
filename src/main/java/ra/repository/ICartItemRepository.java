package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.model.entity.CartItem;

@Repository
public interface ICartItemRepository extends JpaRepository<CartItem, Long> {
}
