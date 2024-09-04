package ra.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.constans.OrderStatus;
import ra.exception.SimpleException;
import ra.model.dto.request.OrderRequest;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.OrderDetail;
import ra.model.entity.Orders;
import ra.repository.OrderDetailRepository;
import ra.service.OrderService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class OrderManagerController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @GetMapping("/getAllOrderItem")
    public ResponseEntity<?> findAllOrderItem() throws SimpleException {
        List<Orders> ordersList = orderService.findAll();
        return ResponseEntity.ok().body(new SimpleResponse(ordersList, HttpStatus.OK));
    }

    @GetMapping("/getOrderStatus/{orderStatus}")
    public ResponseEntity<?> findOrderStatus(@PathVariable("orderStatus") OrderStatus orderStatus) throws SimpleException {
        List<Orders> ordersList = orderService.findByOrderStatus(orderStatus);
        return ResponseEntity.ok().body(new SimpleResponse(ordersList, HttpStatus.OK));

    }
    @GetMapping("/getOrderDetailByOrderId/{orderId}")
    public ResponseEntity<?> findOrderDetailByOrderId(@PathVariable("orderId") Long orderId) throws SimpleException {
        List<OrderDetail> orderDetailList = orderDetailRepository.findByOrderOrderId(orderId);
        return ResponseEntity.ok().body(new SimpleResponse(orderDetailList, HttpStatus.OK));
    }

    //thay dou trang thai doi hang
    @PutMapping("/updateOrderStatus/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrderRequest orderRequest) throws SimpleException {
        Orders newOrder = orderService.findById(orderId);
        newOrder.setOrderStatus(orderRequest.getOrderStatus());
        orderService.update(orderId,orderRequest.getOrderStatus());
        return ResponseEntity.ok().body(new SimpleResponse(newOrder, HttpStatus.OK));
    }



}
