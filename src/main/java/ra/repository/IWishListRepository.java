package ra.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ra.model.entity.Product;
import ra.model.entity.Users;
import ra.model.entity.WishList;

import java.util.List;

@Repository
public interface IWishListRepository extends JpaRepository<WishList, Integer> {

    //TIM TRONG 1 ITEM CO TON TAI CA USER VA PRODUCT KO?
    WishList findByUsersAndProduct(Users user, Product product);

    //LAY RA MOT LIST WISH LIST THEO USERID
    List<WishList> findByUsersUserId(Long userId);

    @Transactional
    void deleteByUsersUserId(Long userId);
}
