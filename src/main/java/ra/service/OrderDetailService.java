package ra.service;

import ra.constans.OrderStatus;
import ra.exception.SimpleException;
import ra.model.entity.Product;

import java.util.List;

public interface OrderDetailService {
    List<Product> getTopSellingProducts(OrderStatus status, int limit) throws SimpleException;

}
