package ra.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.ProductRequest;
import ra.model.entity.Product;

import java.util.List;

public interface IProductService {
    Page<Product> findAll(Pageable pageable) throws CustomException;
    Product save(ProductRequest productRequest,MultipartFile file) throws SimpleException;
    Product getProductById(Long productId) throws SimpleException;
    void delete(Long productId) throws SimpleException;
    Product update(Long productId,ProductRequest productRequest, MultipartFile file) throws SimpleException;
    List<Product> findProductCategoryId(Long categoryId) throws SimpleException;
    List<Product> getNewestProducts(int limit) throws SimpleException;
    Page<Product> getProductByStatusTrue(Pageable pageable) throws CustomException;
    List<Product> searchProductByNameOrDescription(String keyWord) throws SimpleException;
    void updateProductStatus(Long productId, Boolean status) throws SimpleException;
}
