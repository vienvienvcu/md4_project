package ra.service;

import jakarta.transaction.Transactional;
import ra.constans.OrderStatus;
import ra.exception.SimpleException;
import ra.model.dto.request.OrderRequest;
import ra.model.dto.response.OrderDetailResponse;
import ra.model.entity.Orders;

import java.util.List;

public interface OrderService {
    Orders update(Long orderId, OrderStatus newStatus) throws SimpleException;
    Orders findById(Long orderId) throws SimpleException;
    void delete(Long orderId) throws SimpleException;
    List<Orders> findAll() throws SimpleException;
    Orders placeOrderForAllItems(Long userId, OrderRequest orderRequest) throws SimpleException;
    Orders placeOrderWithSelectedItems(Long userId, List<Long> selectedItemIds, OrderRequest orderRequest) throws SimpleException;
    List<Orders> findByUsersUserId(Long userId) throws SimpleException;
    Orders findByOrderStatusAndOrderId(OrderStatus orderStatus, Long orderId) throws SimpleException;
    Orders findBySerial(String serial) throws SimpleException;
    @Transactional
    OrderDetailResponse getOrderDetailBySerial(Long userId, String serialNumber) throws SimpleException;
    List<Orders> findByOrderStatus(OrderStatus status) throws SimpleException;


}
