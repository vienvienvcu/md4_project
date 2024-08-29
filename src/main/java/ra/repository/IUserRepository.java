package ra.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ra.model.entity.Users;

import java.util.List;
import java.util.Optional;
@Repository
public interface IUserRepository extends JpaRepository<Users, Long> {
    Optional<Users>findByEmail(String email);
    Optional<Users> findByUserName(String userName); // Thêm phương thức tìm theo username xem da ton tai chua?
    boolean existsByPhone(String phone);
    Page<Users> findByUserNameContainingIgnoreCase(String username, Pageable pageable);
    List<Users> findByUserNameContainingIgnoreCase(String username);
    @Modifying
    @Query("UPDATE Users u SET u.status = :status WHERE u.userId = :userId")
    void updateUsersByStatus(Long userId, Boolean status);
}
