package ra.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ra.constans.OrderStatus;
import ra.exception.SimpleException;
import ra.model.entity.Product;
import ra.repository.IProductRepository;
import ra.repository.OrderDetailRepository;
import ra.service.OrderDetailService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private IProductRepository productRepository; // Để lấy thông tin sản phẩm
    @Override
    public List<Product> getTopSellingProducts(OrderStatus status, int limit) throws SimpleException {
        List<Object[]> results = orderDetailRepository.findTopSellingProducts(status);
        List<Product> topProducts = new ArrayList<>();

        for (int i = 0; i < Math.min(limit, results.size()); i++) {
            Long productId = (Long) results.get(i)[0];
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new SimpleException("Product not found: " + productId, HttpStatus.NOT_FOUND));
            if (product != null) {
                topProducts.add(product);
            }
        }
        return topProducts;
    }
}
