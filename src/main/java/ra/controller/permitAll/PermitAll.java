package ra.controller.permitAll;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.constans.OrderStatus;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.response.DataResponse;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Categories;
import ra.model.entity.Product;
import ra.repository.IProductRepository;
import ra.service.ICategoryService;
import ra.service.IProductService;
import ra.service.OrderDetailService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
public class PermitAll {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private OrderDetailService orderDetailService;

    // Chi tiết thông tin sản phẩm theo id - Bắt buộc

    @GetMapping("/getProductById/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable("productId") Long productId) throws SimpleException {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok().body(new SimpleResponse(product, HttpStatus.OK));
    }

   // danh sach san pham theo danh muc
    @GetMapping("/getProductByCategory/{categoryId}")
    public ResponseEntity<?> getProductByCategory(@PathVariable("categoryId") Long categoryId) throws SimpleException {
        List<Product> productByCategory = productService.findProductCategoryId(categoryId);
        return ResponseEntity.ok().body(new SimpleResponse(productByCategory, HttpStatus.OK));
    }

  //  Danh sách sản phẩm mới

    @GetMapping("/getNewProduct")
    public ResponseEntity<?> getNewProduct(@RequestParam(defaultValue = "5") int limit) throws SimpleException {
        List<Product> productList = productService.getNewestProducts(limit);
        return ResponseEntity.ok().body(new SimpleResponse(productList, HttpStatus.OK));
    }

    //   Danh sách sản phẩm được bán(có phân trang và sắp xếp)
    @GetMapping("/allProductByStatus")
    public ResponseEntity<?> getAllProductByStatus(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "3") int size,
                                                   @RequestParam(defaultValue = "asc") String sortDirection) throws CustomException {

        // Xác định hướng sắp xếp
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "productName"));
        // goi trong csdl
        Page<Product> productsPage = productService.getProductByStatusTrue(pageable);
        return ResponseEntity.ok().body(new DataResponse<>(
                productsPage.getContent(),
                HttpStatus.OK,
                productsPage.getTotalPages(), // Tổng số trang
                productsPage.getTotalElements(), // Tổng số phần tử
                productsPage.getNumber(), // Trang hiện tại
                productsPage.getNumberOfElements() // Số lượng phần tử trong trang hiện tại
        ));
    }

  //   Tìm kiếm sản phẩm theo tên hoặc mô tả

    @GetMapping("/getProductByKeyWord")
    public ResponseEntity<?> getProductByKeyWord(@RequestParam String keyWord) throws SimpleException {
        List<Product> productList = productService.searchProductByNameOrDescription(keyWord);
        return ResponseEntity.ok().body(new SimpleResponse(productList, HttpStatus.OK));
    }

  //   Danh sách danh mục được bán - Không bắt buộc

    @GetMapping("/getCategoryByStatus")
    public ResponseEntity<?> getCategoryByStatus() throws SimpleException{
        List<Categories> categories = categoryService.getAllCategoriesByStatus();
        return ResponseEntity.ok().body(new SimpleResponse(categories, HttpStatus.OK));
    }



  //  Danh sách sản phẩm bán chạy - Không bắt buộc

    @GetMapping("/topSellingProducts")
    public ResponseEntity<?> getTopSellingProducts(
            @RequestParam(defaultValue = "SUCCESS") OrderStatus orderStatus,
            @RequestParam(name = "limit", defaultValue = "5") int limit) throws SimpleException {

        List<Product> topProducts = orderDetailService.getTopSellingProducts(orderStatus, limit);
        return ResponseEntity.ok().body(new SimpleResponse(topProducts, HttpStatus.OK));
    }

  //  Danh sách sản phẩm nổi bật - Không bắt buộc



}
