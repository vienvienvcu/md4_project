package ra.service;

import org.springframework.transaction.annotation.Transactional;
import ra.exception.SimpleException;
import ra.model.dto.request.CartItemRequest;
import ra.model.entity.CartItem;

import java.util.List;

public interface ICartItemService {
    List<CartItem> getCartItems() throws SimpleException;
    CartItem getCartItemById(Long cartItemId) throws SimpleException;
    CartItem saveCartItem(CartItemRequest cartItemRequest, Long userId) throws SimpleException;
    CartItem updateCartItem(Long cartItemId, CartItemRequest cartItemRequest, Long userId) throws SimpleException;
    void deleteCartItem(Long cartItemId) throws SimpleException;
    @Transactional
    void deleteAllCartItems(Long userId) throws SimpleException;
}
