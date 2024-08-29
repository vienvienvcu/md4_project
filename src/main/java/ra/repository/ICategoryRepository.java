package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.model.entity.Categories;

import java.util.List;

@Repository
public interface ICategoryRepository extends JpaRepository<Categories, Long> {
    Boolean existsByCategoryName(String categoryName);
    List<Categories>findCategoriesByStatusTrue();

}
