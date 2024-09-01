package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ra.constans.OrderStatus;
import ra.model.entity.Orders;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUsersUserId(Long userId);
    List<Orders> findByOrderStatusAndUsersUserId(OrderStatus orderStatus, Long userId);
    Orders findBySerialNumber(String serialNumber);
    List<Orders> findByOrderStatus(OrderStatus status);
}
