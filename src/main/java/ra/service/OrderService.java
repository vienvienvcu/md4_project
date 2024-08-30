package ra.service;

import ra.constans.OrderStatus;
import ra.exception.SimpleException;
import ra.model.dto.request.OrderRequest;
import ra.model.entity.Orders;

import java.util.List;

public interface OrderService {
    Orders update(Long orderId, Orders order) throws SimpleException;
    Orders findById(Long orderId) throws SimpleException;
    void delete(Long orderId) throws SimpleException;
    List<Orders> findAll() throws SimpleException;
    Orders placeOrderForAllItems(Long userId, OrderRequest orderRequest) throws SimpleException;
    Orders placeOrderWithSelectedItems(Long userId, List<Long> selectedItemIds, OrderRequest orderRequest) throws SimpleException;
    List<Orders> findByUsersUserId(Long userId) throws SimpleException;
    List<Orders> findByOrderStatusAndUserId(OrderStatus orderStatus, Long userId) throws SimpleException;
}
