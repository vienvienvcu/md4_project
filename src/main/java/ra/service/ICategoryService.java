package ra.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.entity.Categories;

import java.util.List;

public interface ICategoryService {
    Page<Categories> findAll(Pageable pageable) throws CustomException;
    Categories findById(Long categoryId) throws SimpleException;
    Categories insert(Categories category) throws SimpleException;
    Categories update(Long categoryId,Categories category) throws SimpleException;
    void delete(Long categoryId) throws SimpleException;
    List<Categories> getAllCategoriesByStatus() throws SimpleException;


}
