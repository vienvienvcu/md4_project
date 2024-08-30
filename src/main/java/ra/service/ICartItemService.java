package ra.service;

import org.springframework.transaction.annotation.Transactional;
import ra.exception.SimpleException;
import ra.model.entity.CartItem;

import java.util.List;

public interface ICartItemService {
    List<CartItem> getCartItems() throws SimpleException;
    CartItem getCartItemById(Long cartItemId) throws SimpleException;
    CartItem saveCartItem(CartItem cartItem) throws SimpleException;
    CartItem updateCartItem(Long cartItemId,CartItem cartItem) throws SimpleException;
    void deleteCartItem(Long cartItemId) throws SimpleException;
    @Transactional
    void deleteAllCartItems(Long userId) throws SimpleException;
}
