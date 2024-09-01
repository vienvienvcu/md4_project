package ra.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.constans.OrderStatus;
import ra.exception.SimpleException;
import ra.model.dto.request.OrderRequest;
import ra.model.dto.response.OrderDetailResponse;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Orders;
import ra.security.principle.MyUserDetails;
import ra.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class OrderController {
    @Autowired
    private OrderService orderService;


    @PostMapping("/addOrderAllCart")
    public ResponseEntity<?> addOrder(@Valid @RequestBody OrderRequest orderRequest) throws SimpleException {
        // Lấy thông tin người dùng từ SecurityContext
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        Orders order = orderService.placeOrderForAllItems(userId, orderRequest);
        return ResponseEntity.ok().body(new SimpleResponse(order, HttpStatus.CREATED));

    }

    @PostMapping("/addOrderSelectCart")
    public ResponseEntity<?> addOrderSelectCart(@Valid @RequestBody OrderRequest orderRequest,
                                                @RequestParam List<Long> item) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        Orders order = orderService.placeOrderWithSelectedItems(userId, item, orderRequest);
        return ResponseEntity.ok().body(new SimpleResponse(order, HttpStatus.CREATED));

    }

    @GetMapping("/getAllOrder")
    public ResponseEntity<?> getAllOrder() throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Lấy tất cả các mục ORDER của người dùng
        List<Orders> orderList = orderService.findByUsersUserId(userId);

        return ResponseEntity.ok().body(new SimpleResponse(orderList, HttpStatus.OK));
    }

    @GetMapping("/getOrderByStatus/{orderStatus}")
    public ResponseEntity<?> getOrderByStatus(@PathVariable("orderStatus") OrderStatus orderStatus) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        List<Orders> orderList = orderService.findByOrderStatusAndUserId(orderStatus, userId);
        return ResponseEntity.ok().body(new SimpleResponse(orderList, HttpStatus.OK));
    }

    @GetMapping("/getOrderDetailBySerial/{serialNumber}")
    public ResponseEntity<?> getOrderDetailBySerial(@PathVariable("serialNumber") String serialNumber) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        // Lấy chi tiết đơn hàng theo serialNumber
        OrderDetailResponse orderDetailResponse = orderService.getOrderDetailBySerial(userId, serialNumber);
        if (orderDetailResponse == null) {
            throw new SimpleException("Order not found.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok().body(new SimpleResponse(orderDetailResponse, HttpStatus.OK));
    }

    @PutMapping("/updateStatus/{orderId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId, @Valid @RequestBody OrderRequest orderRequest) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Tìm đơn hàng theo orderId
        Orders order = orderService.findById(orderId);
        if (order == null) {
            throw new SimpleException("Order not found.", HttpStatus.NOT_FOUND);
        }
        // Kiểm tra quyền truy cập
        if (!order.getUsers().getUserId().equals(userId)) {
            throw new SimpleException("Unauthorized access to the order.", HttpStatus.FORBIDDEN);
        }
        // Cập nhật trạng thái đơn hàng
        order.setOrderStatus(orderRequest.getOrderStatus());

        return ResponseEntity.ok().body(new SimpleResponse(orderService.update(orderId,orderRequest), HttpStatus.OK));

    }
}



