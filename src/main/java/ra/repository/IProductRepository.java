package ra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.model.entity.Product;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {
    boolean existsByCategoryCategoryId(Long categoryId);

    boolean existsByProductName(String productName);
    List<Product> findProductByCategoryCategoryId(Long categoryId);
    Page<Product> findAllByOrderByUpdateTimeDesc(Pageable pageable);
    Page<Product> findProductByStatusTrue(Pageable pageable);
    List<Product> findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String productName, String description);
}

