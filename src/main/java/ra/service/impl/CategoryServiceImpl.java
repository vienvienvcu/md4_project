package ra.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ra.exception.SimpleException;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Categories;
import ra.model.entity.Product;
import ra.repository.ICategoryRepository;
import ra.repository.IProductRepository;
import ra.service.ICategoryService;

import java.util.List;
@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    public Page<Categories> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Categories findById(Long categoryId) throws SimpleException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new SimpleException("not exist " + categoryId, HttpStatus.NOT_FOUND));
    }
   @Override
    public Categories insert(Categories category) throws SimpleException {

        if (categoryRepository.existsByCategoryName(category.getCategoryName())) {
            throw new SimpleException("Category name already exists: " + category.getCategoryName(), HttpStatus.BAD_REQUEST);
        }
        return categoryRepository.save(category);
    }

    @Override
    public Categories update(Long categoryId, Categories category) throws SimpleException {
        // Kiểm tra sự tồn tại của danh mục
        Categories existingCategory =categoryRepository.findById(categoryId)
                .orElseThrow(() -> new SimpleException("Sản phẩm không tìm thấy", HttpStatus.NOT_FOUND));
        // Nếu có tên sản phẩm mới, kiểm tra xem tên đó có trùng với các tên sản phẩm khác không (ngoài tên của sản phẩm hiện tại)
        if (category.getCategoryName() != null &&
                !category.getCategoryName().equals(existingCategory.getCategoryName())){
            throw new SimpleException("Tên sản phẩm đã tồn tại: "
                    + category.getCategoryName(), HttpStatus.BAD_REQUEST);
        }
        return categoryRepository.save(category);
    }

    @Override
    public void delete(Long categoryId) throws SimpleException {

        // Kiểm tra sự tồn tại của danh mục
       if (categoryId == null || !categoryRepository.existsById(categoryId)){
           throw new SimpleException("Category with ID " + categoryId + " does not exist", HttpStatus.NOT_FOUND);
       }
        // Kiểm tra xem có sản phẩm nào đang sử dụng danh mục này không
        if (productRepository.existsByCategoryCategoryId(categoryId)) {
            throw new SimpleException("Cannot delete category with ID " + categoryId + " as it is being used by products", HttpStatus.BAD_REQUEST);
        }
        // Nếu không có sản phẩm nào sử dụng danh mục, thực hiện xóa
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public List<Categories> getAllCategoriesByStatus() throws SimpleException {
        return categoryRepository.findCategoriesByStatusTrue();
    }

}
