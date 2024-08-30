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
                                                @RequestParam List<Long> selectedItemIds) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        Orders order = orderService.placeOrderWithSelectedItems(userId, selectedItemIds, orderRequest);
        return ResponseEntity.ok().body(new SimpleResponse(order, HttpStatus.CREATED));

    }

    @GetMapping("/getAllOrder")
    public ResponseEntity<?> getAllOrder() throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Lấy tất cả các mục ORDER của người dùng
        List<Orders> OrderList = orderService.findByUsersUserId(userId);

        return ResponseEntity.ok().body(new SimpleResponse(OrderList, HttpStatus.OK));
    }

    @GetMapping("getOrderByStatus/{orderStatus}")
    public ResponseEntity<?> getOrderByStatus(@PathVariable("orderStatus") OrderStatus orderStatus) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        List<Orders> orderList = orderService.findByOrderStatusAndUserId(orderStatus, userId);
        return ResponseEntity.ok().body(new SimpleResponse(orderList, HttpStatus.OK));
    }

}



