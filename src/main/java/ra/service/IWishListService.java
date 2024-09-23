package ra.service;

import org.springframework.transaction.annotation.Transactional;
import ra.exception.SimpleException;
import ra.model.dto.request.WishListRequest;
import ra.model.entity.WishList;

import java.util.List;

public interface IWishListService {
    List<WishList> getWishList() throws SimpleException;
    WishList getWishListById(Integer wishListId) throws SimpleException;
    WishList saveWishList(Long userId, WishListRequest wishListRequest) throws SimpleException;
    void deleteWishList(Integer wishListId) throws SimpleException;
    @Transactional
    void deleteAllWishList(Long userId) throws SimpleException;
}
