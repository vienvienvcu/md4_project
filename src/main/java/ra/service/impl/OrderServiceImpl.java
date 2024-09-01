package ra.service.impl;


import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ra.constans.OrderStatus;
import ra.exception.SimpleException;

import ra.model.dto.request.OrderRequest;
import ra.model.dto.response.OrderDetailResponse;
import ra.model.entity.*;

import ra.repository.ICartItemRepository;
import ra.repository.IProductRepository;
import ra.repository.OrderDetailRepository;
import ra.repository.OrderRepository;
import ra.service.ICartItemService;
import ra.service.IProductService;
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

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    public Orders update(Long orderId, OrderRequest orderRequest) throws SimpleException {
        Orders orders = findById(orderId);
        if (orders == null) {
            throw new SimpleException("Order not found", HttpStatus.NOT_FOUND);
        }
        orders.setOrderStatus(orderRequest.getOrderStatus());
        return orderRepository.save(orders);
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
        Double totalPrice = cartItemList.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
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
                .users(user)
                .build();
        //save vao csdl

        orderRepository.save(order);

        // Add cart items to order detail

        for (CartItem cartItem : cartItemList) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .productName(cartItem.getProduct().getProductName())
                    .productPrice(cartItem.getProduct().getPrice())
                    .productQuantity(cartItem.getQuantity())
                    .order(order)
                    .product(cartItem.getProduct())
                    .build();
            orderDetailRepository.save(orderDetail);
            // Cập nhật tồn kho
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }
        // Xóa các mặt hàng trong giỏ hàng
        cartItemRepository.deleteAll(cartItemList);
        return order;
    }

    // viet ham rieng cua ngay update

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
        List<CartItem> cartItemList = cartItemRepository.findByUsersUserIdAndCartIdIn(userId, selectedItemIds);

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
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
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
                .receivedTime(calculateReceivedTime(new Date()))
                .users(user)
                .build();

        orderRepository.save(order);

        for (CartItem cartItem : selectedItems) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .productName(cartItem.getProduct().getProductName())
                    .productPrice(cartItem.getProduct().getPrice())
                    .productQuantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .order(order)
                    .build();

            orderDetailRepository.save(orderDetail);
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

        }
        // Clear the cart
        cartItemRepository.deleteAll(cartItemList);
        return order;


    }

    @Override
    public List<Orders> findByUsersUserId(Long userId) throws SimpleException {
        return orderRepository.findByUsersUserId(userId);
    }

    @Override
    public List<Orders> findByOrderStatusAndUserId(OrderStatus orderStatus, Long userId) throws SimpleException {
        return orderRepository.findByOrderStatusAndUsersUserId(orderStatus,userId);
    }

    @Override
    public Orders findBySerial(String serial) throws SimpleException {
        return orderRepository.findBySerialNumber(serial);
    }
    @Transactional
    @Override
    public OrderDetailResponse getOrderDetailBySerial(Long userId, String serialNumber) throws SimpleException {
        // Tìm đơn hàng theo serialNumber
        Orders order = orderRepository.findBySerialNumber(serialNumber);

        if (order == null) {
            throw new SimpleException("Order is empty.",HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra quyền truy cập của người dùng
        if (!order.getUsers().getUserId().equals(userId)) {
            throw new SimpleException("Unauthorized access to order.",HttpStatus.BAD_REQUEST);
        }

        // Lấy chi tiết đơn hàng
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(order.getOrderId());
        OrderDetailResponse response = new OrderDetailResponse();
        response.setOrderDetails(orderDetails);
        return response;
    }

    @Override
    public List<Orders> findByOrderStatus(OrderStatus status) throws SimpleException {
        return orderRepository.findByOrderStatus(status);
    }


}
