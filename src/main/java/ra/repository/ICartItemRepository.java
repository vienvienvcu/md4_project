package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.model.entity.CartItem;
import ra.model.entity.Product;
import ra.model.entity.Users;

import java.util.List;

@Repository
public interface ICartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUsersAndProduct(Users user, Product product);
    List<CartItem> findByUsersUserId(Long userId);
    @Transactional
    void deleteByUsersUserId(Long userId);
}
