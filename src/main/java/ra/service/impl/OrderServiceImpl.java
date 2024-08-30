package ra.service.impl;


import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ra.constans.OrderStatus;
import ra.exception.SimpleException;

import ra.model.dto.request.OrderRequest;
import ra.model.entity.CartItem;
import ra.model.entity.Orders;

import ra.model.entity.OrderDetail;
import ra.model.entity.Users;
import ra.repository.ICartItemRepository;
import ra.repository.OrderRepository;
import ra.service.ICartItemService;
import ra.service.IUserService;
import ra.service.OrderService;

import java.util.*;


@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ICartItemRepository cartItemRepository;

    @Autowired
    private ICartItemService cartItemService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IUserService userService;

    @Override
    public Orders update(Long orderId, Orders order) throws SimpleException {
        return null;
    }

    @Override
    public Orders findById(Long orderId) throws SimpleException {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new SimpleException("Order not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public void delete(Long orderId) throws SimpleException {
        if (orderId== null || !orderRepository.existsById(orderId)) {
            throw new SimpleException("product id not exists: " + orderId , HttpStatus.NOT_FOUND);
        }
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<Orders> findAll() throws SimpleException {
        return orderRepository.findAll();
    }

    //Người dùng mua tất cả các mục có trong giỏ hàng của họ.

    @Override
    public Orders placeOrderForAllItems(Long userId, OrderRequest orderRequest) throws SimpleException {

        // Xác thực người dùng với userId
        Users user =  userService.getUserById(userId);

        // Kiểm tra giỏ hàng của người dùng

        List<CartItem> cartItemList = cartItemRepository.findByUsersUserId(userId);

        // Kiểm tra giỏ hàng của người dùng

        if (cartItemList.isEmpty()){
            throw new SimpleException("CartItem is empty", HttpStatus.BAD_REQUEST);
        }
        // Calculate total price
        Double totalPrice =cartItemList.stream()
                .mapToDouble(item -> item.getProduct().getStock() * item.getQuantity())
                .sum();


        // Sinh UUID ngẫu nhiên cho serial Number
        String serialNumber = orderRequest.getSerialNumber();
        if (serialNumber == null || serialNumber.isEmpty()) {
            serialNumber = UUID.randomUUID().toString();
        }


        // TAO DON HANG MOI
        Orders order = Orders.builder()
                .serialNumber(serialNumber)
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.WAITING)
                .receiveName(orderRequest.getReceiveName())
                .receiveAddress(orderRequest.getReceiveAddress())
                .receivePhone(orderRequest.getReceivePhone())
                .createTime(new Date())
                .receivedTime(calculateReceivedTime(new Date())) // Calculate receivedTime
                .orderDetails(new HashSet<>())
                .users(user)
                .build();

        // Add cart items to order

        for (CartItem cartItem : cartItemList) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .productName(cartItem.getProduct().getProductName())
                    .productPrice(cartItem.getProduct().getPrice())
                    .productQuantity(cartItem.getQuantity())
                    .order(order)
                    .product(cartItem.getProduct())
                    .build();
            order.getOrderDetails().add(orderDetail);
        }
        // Save the order
        Orders savedOrder = orderRepository.save(order);
        // Clear the cart
        cartItemService.deleteAllCartItems(userId);
        return savedOrder;
    }

    private Date calculateReceivedTime(Date createTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(createTime);
        calendar.add(Calendar.DAY_OF_YEAR, 4); // Add 4 days
        return calendar.getTime();
    }
    @Override
    public Orders placeOrderWithSelectedItems(Long userId, List<Long> selectedItemIds, OrderRequest orderRequest) throws SimpleException {
        Users user =  userService.getUserById(userId);

        // Get cart items
        List<CartItem> cartItemList = cartItemRepository.findByUsersUserId(userId);

        // Filter selected items
        List<CartItem> selectedItems = new ArrayList<>();
        for (CartItem item : cartItemList) {
            if (selectedItemIds.contains(item.getCartId())) {
                selectedItems.add(item);
            }
        }
        if (selectedItems.isEmpty()) {
            throw new SimpleException("No items selected", HttpStatus.BAD_REQUEST);
        }
        // Calculate total price
        Double totalPrice = selectedItems.stream()
                .mapToDouble(item -> item.getProduct().getStock() * item.getQuantity())
                .sum();


        // Sinh UUID ngẫu nhiên cho serial Number
        String serialNumber = orderRequest.getSerialNumber();
        if (serialNumber == null || serialNumber.isEmpty()) {
            serialNumber = UUID.randomUUID().toString();
        }


        // TAO DON HANG MOI
        Orders order = Orders.builder()
                .serialNumber(serialNumber)
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.WAITING)
                .receiveName(orderRequest.getReceiveName())
                .receiveAddress(orderRequest.getReceiveAddress())
                .receivePhone(orderRequest.getReceivePhone())
                .createTime(new Date())
                .receivedTime(calculateReceivedTime(new Date())) // Calculate receivedTime
                .build();

        // Add selected items to order
        for (CartItem cartItem : selectedItems) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .productName(cartItem.getProduct().getProductName())
                    .productPrice(cartItem.getProduct().getPrice())
                    .productQuantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .order(order)
                    .build();
            order.getOrderDetails().add(orderDetail);
        }
        // Save the order
        Orders savedOrder = orderRepository.save(order);
        // Clear the cart
        cartItemService.deleteAllCartItems(userId);
        return savedOrder;


    }

    @Override
    public List<Orders> findByUsersUserId(Long userId) throws SimpleException {
        return orderRepository.findByUsersUserId(userId);
    }

    @Override
    public List<Orders> findByOrderStatusAndUserId(OrderStatus orderStatus, Long userId) throws SimpleException {
        return orderRepository.findByOrderStatusAndUsersUserId(orderStatus,userId);
    }


}
