package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ra.constans.OrderStatus;
import ra.model.entity.OrderDetail;

import java.util.List;
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderOrderId(Long orderId);
    Boolean existsByProductProductId(Long productId);

    @Query("SELECT od.product.productId, SUM(od.productQuantity) AS totalSold " +
            "FROM OrderDetail od JOIN od.order o " +
            "WHERE o.orderStatus = :status " +
            "GROUP BY od.product.productId " +
            "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts(@Param("status") OrderStatus status);
}
