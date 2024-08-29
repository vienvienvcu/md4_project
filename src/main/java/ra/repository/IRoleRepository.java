package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.constans.RoleName;
import ra.model.entity.Roles;

import java.util.Optional;
@Repository
public interface IRoleRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findByRoleName(RoleName roleName);
}
