package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ra.model.entity.Address;

import java.util.List;

@Repository
public interface IAddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByUserUserId(Long userId);
    Address findByAddressIdAndUserUserId(Integer addressId, Long userId);
    boolean existsByFullAddress(String fullAddress);
    Address findByFullAddressAndAddressIdNot(String fullAddress, Integer addressId);
}
