package ra.service.impl;

import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ra.exception.SimpleException;
import ra.model.entity.Product;
import ra.model.entity.Users;
import ra.model.entity.WishList;
import ra.repository.IProductRepository;
import ra.repository.IUserRepository;
import ra.repository.IWishListRepository;
import ra.service.IWishListService;

import java.util.List;

@Service
public class WishListServiceImpl implements IWishListService {

    @Autowired
    private IWishListRepository wishListRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override
    public List<WishList> getWishList() {
        return wishListRepository.findAll();
    }

    @Override
    public WishList getWishListById(Integer wishListId) throws SimpleException {
        return wishListRepository.findById(wishListId)
                .orElseThrow(()-> new SimpleException("Not pound"+ wishListId, HttpStatus.NOT_FOUND));
    }

    @Override
    public WishList saveWishList(WishList wishList) throws SimpleException {

        // Kiểm tra sự tồn tại của sản phẩm và người dùng

        Product product = productRepository.findById(wishList.getProduct().getProductId())
                .orElseThrow(()-> new SimpleException("Product not found",HttpStatus.NOT_FOUND));

        Users users = userRepository.findById(wishList.getUsers().getUserId())
                .orElseThrow(()-> new SimpleException("User not found",HttpStatus.NOT_FOUND));

       //   kiem tra trong 1 list wishList da ton tai san phan va user chua??

        WishList existtingWishList = wishListRepository.findByUsersAndProduct(users,product);
        if(existtingWishList != null){
            // xoa cai san pham co trong wishList di
           wishListRepository.delete(existtingWishList);
        }

        // Thiết lập sản phẩm và người dùng cho wishList
        wishList.setProduct(product);
        wishList.setUsers(users);
        return wishListRepository.save(wishList);

    }

    @Override
    public void deleteWishList(Integer wishListId) {
        wishListRepository.deleteById(wishListId);
    }

    @Override
    public void deleteAllWishList(Long userId) throws SimpleException {
        wishListRepository.deleteByUsersUserId(userId);
    }
}
