package ra.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.ProductRequest;
import ra.model.entity.Product;
import ra.repository.*;
import ra.service.ICategoryService;
import ra.service.IProductService;
import ra.service.IUploadFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IUploadFile uploadFile;

    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private IWishListRepository wishListRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ICartItemRepository cartItemRepository;

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product save(ProductRequest productRequest, MultipartFile file) throws SimpleException {
        if (productRepository.existsByProductName(productRequest.getProductName())) {
            throw new SimpleException("product name already exists: " +
                    productRequest.getProductName(), HttpStatus.CONFLICT);
        }

        // Sinh UUID ngẫu nhiên cho sku
        String sku = productRequest.getSku();
        if (sku == null || sku.isEmpty()) {
            sku = UUID.randomUUID().toString();
        }

        Product product = Product.builder()
                .sku(sku)
                .productName(productRequest.getProductName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .stock(productRequest.getStock())
                .status(productRequest.getStatus())
                .createTime(new Date())
                .updateTime(new Date())
                .category(categoryRepository.findById(productRequest.getCategoryId())
                        .orElseThrow(()-> new SimpleException("Category not found", HttpStatus.NOT_FOUND)))
                .build();
        if (file != null && !file.isEmpty()) {
            String imageUrl = uploadFile.uploadLocal(file);
            product.setImage(imageUrl);
        }
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long productId) throws SimpleException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new SimpleException("product not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public void delete(Long productId) throws SimpleException {
        if (productId== null || !productRepository.existsById(productId)) {
            throw new SimpleException("product id not exists: " + productId , HttpStatus.NOT_FOUND);
        }

        // Kiểm tra xem sản phẩm có tồn tại trong danh mục không
        if (productRepository.existsByCategoryCategoryId(productId)) {
            throw new SimpleException("Product exists in a category and cannot be deleted: " + productId, HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra xem sản phẩm có tồn tại trong đơn hàng không
        if (orderDetailRepository.existsByProductProductId(productId)) {
            throw new SimpleException("Product is already part of an order and cannot be deleted: " + productId, HttpStatus.CONFLICT);
        }

        // update lai trang thai product false
        if (wishListRepository.existsByProductProductId(productId)) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new SimpleException("Product not found: " + productId, HttpStatus.NOT_FOUND));
            product.setStatus(false); // Thay đổi trạng thái thành không hoạt động
            productRepository.save(product); // Lưu thay đổi
//            wishListRepository.deleteByProductProductId(productId);
        }

        // update lai trang thai product ve false
        if (cartItemRepository.existsByProductProductId(productId)) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new SimpleException("Product not found: " + productId, HttpStatus.NOT_FOUND));
            product.setStatus(false); // Thay đổi trạng thái thành không hoạt động
            productRepository.save(product); // Lưu thay đổi
//            cartItemRepository.deleteByProductProductId(productId);
        }

        // Xóa sản phẩm khỏi cơ sở dữ liệu
        productRepository.deleteById(productId);

    }

    @Override
    public Product update(Long productId, ProductRequest productRequest, MultipartFile file) throws SimpleException {
        // Tìm sản phẩm cần cập nhật
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new SimpleException("Sản phẩm không tìm thấy", HttpStatus.NOT_FOUND));
        // Nếu có tên sản phẩm mới, kiểm tra xem tên đó có trùng với các tên sản phẩm khác không (ngoài tên của sản phẩm hiện tại)
        if (productRequest.getProductName() != null &&
                productRequest.getProductName().equals(existingProduct.getProductName())) {
            throw new SimpleException("Tên sản phẩm đã tồn tại: "
                    + productRequest.getProductName(), HttpStatus.BAD_REQUEST);
        }
        // Cập nhật các thông tin sản phẩm
        existingProduct.setProductName(productRequest.getProductName());
        existingProduct.setDescription(productRequest.getDescription());
        existingProduct.setPrice(productRequest.getPrice());
        existingProduct.setStock(productRequest.getStock());
        existingProduct.setStatus(productRequest.getStatus());
        existingProduct.setUpdateTime(new Date());
        // Cập nhật danh mục
        existingProduct.setCategory(categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new SimpleException("Danh mục không tìm thấy", HttpStatus.NOT_FOUND)));

        if (file != null && !file.isEmpty()) {
            String imageUrl = uploadFile.uploadLocal(file);
            existingProduct.setImage(imageUrl);
        }
        // Lưu sản phẩm đã cập nhật vào cơ sở dữ liệu
        return productRepository.save(existingProduct);

    }

    @Override
    public List<Product> findProductCategoryId(Long categoryId) throws SimpleException {
        if (categoryId == null || !categoryRepository.existsById(categoryId)) {
            throw new SimpleException("Category not found", HttpStatus.NOT_FOUND);
        }
        return productRepository.findProductByCategoryCategoryId(categoryId);
    }

    @Override
    public List<Product> getNewestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return productRepository.findAllByOrderByUpdateTimeDesc(pageable).getContent();
    }

    @Override
    public Page<Product> getProductByStatusTrue(Pageable pageable) throws CustomException {
        return productRepository.findProductByStatusTrue(pageable);
    }

    @Override
    public List<Product> searchProductByNameOrDescription(String keyWord) throws SimpleException {
        if (keyWord == null || keyWord.trim().isEmpty()) {
            throw new SimpleException("Keyword cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        List<Product> products = productRepository.findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyWord, keyWord);
        if (products.isEmpty()) {
            throw new SimpleException("No products found for keyword: " + keyWord, HttpStatus.NOT_FOUND);
        }
        return products;
    }

    @Override
    public void updateProductStatus(Long productId, Boolean status) throws SimpleException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new SimpleException("Product not found: " + productId, HttpStatus.NOT_FOUND));
        product.setStatus(false); // Cập nhật trạng thái sản phẩm
        productRepository.save(product);
    }

}
