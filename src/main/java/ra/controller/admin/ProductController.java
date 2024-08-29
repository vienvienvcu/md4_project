package ra.controller.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.ProductRequest;
import ra.model.dto.response.DataResponse;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Product;
import ra.service.IProductService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/admin")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping("/getAllProduct")
    public ResponseEntity<?> getAllProduct(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "3") int size,
                                           @RequestParam(defaultValue = "asc") String sortDirection) throws CustomException {

        // Xác định hướng sắp xếp
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction,"productName"));

        Page<Product> productsPage = productService.findAll(pageable);

        return ResponseEntity.ok().body(new DataResponse(
                productsPage.getContent(),
                HttpStatus.OK,
                productsPage.getTotalPages(), // Tổng số trang
                productsPage.getTotalElements(), // Tổng số phần tử
                productsPage.getNumber(), // Trang hiện tại
                productsPage.getNumberOfElements() // Số lượng phần tử trong trang hiện tại
        ));
    }
    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@Valid @ModelAttribute ProductRequest productRequest) throws SimpleException {
        Product createProduct = productService.save(productRequest, productRequest.getImage());
        return ResponseEntity.created(URI.create("/api/v1/admin/addProduct"+ createProduct.getProductId()))
                .body(new SimpleResponse(createProduct, HttpStatus.CREATED));
    }

    @GetMapping("/getProductById/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId) throws SimpleException {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok().body(new SimpleResponse(product, HttpStatus.OK));
    }

   @PutMapping("/productUpdate/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Long productId,
                                           @Valid @ModelAttribute ProductRequest productRequest,
                                           @RequestParam(value = "file", required = false) MultipartFile file) throws SimpleException {

       // Cập nhật sản phẩm với thông tin
       Product productUpdate = productService.update(productId, productRequest, file);
       return ResponseEntity.ok().body(new SimpleResponse(productUpdate, HttpStatus.OK));
   }

   @DeleteMapping("/deleteProduct/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) throws SimpleException {
        productService.delete(productId);
       return ResponseEntity.ok(new SimpleResponse("Category deleted successfully", HttpStatus.OK));
   }
}
