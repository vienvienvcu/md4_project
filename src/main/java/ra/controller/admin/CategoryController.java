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
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.response.DataResponse;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Categories;
import ra.repository.IProductRepository;
import ra.service.ICategoryService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IProductRepository productRepository;


    @GetMapping("/getAllCategory")
    public ResponseEntity<?> getAllCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "asc") String sortDirection)throws CustomException {

        // Xác định hướng sắp xếp
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "categoryName"));

        // Lấy trang người dùng từ service
        Page<Categories> categoriesPage = categoryService.findAll(pageable);

        return ResponseEntity.ok().body(new DataResponse<>(
                categoriesPage.getContent(), // Nội dung trang
                HttpStatus.OK,
                categoriesPage.getTotalPages(), // Tổng số trang
                categoriesPage.getTotalElements(), // Tổng số phần tử
                categoriesPage.getNumber(), // Trang hiện tại
                categoriesPage.getNumberOfElements() // Số lượng phần tử trong trang hiện tại
        ));
    }

    @PostMapping("/addCategory")
    public ResponseEntity<?> addCategory(@Valid @RequestBody Categories category) throws SimpleException {
        Categories savedCategory = categoryService.insert(category);
        return ResponseEntity.created(URI.create("/api/v1/admin/addCategory"))
                .body(new SimpleResponse(savedCategory, HttpStatus.CREATED)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable long categoryId) throws SimpleException {
        Categories category = categoryService.findById(categoryId);
        return ResponseEntity.ok().body(new SimpleResponse(category, HttpStatus.OK));
    }

    @PutMapping("categoryUpdate/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable long categoryId, @Valid @RequestBody Categories category) throws SimpleException {
        Categories savedCategory = categoryService.update(categoryId, category);
        return ResponseEntity.ok().body(new SimpleResponse(savedCategory, HttpStatus.OK));
    }

    @DeleteMapping ("delete/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable long categoryId) throws SimpleException {
        categoryService.delete(categoryId);
        return ResponseEntity.ok(new SimpleResponse("Category deleted successfully", HttpStatus.OK));
    }

}
