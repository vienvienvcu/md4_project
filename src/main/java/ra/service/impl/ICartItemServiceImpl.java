package ra.service.impl;

import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.exception.SimpleException;
import ra.model.entity.CartItem;
import ra.model.entity.Product;
import ra.model.entity.Users;
import ra.repository.ICartItemRepository;
import ra.repository.IProductRepository;
import ra.repository.IUserRepository;
import ra.service.ICartItemService;

import java.util.List;
@Service
public class ICartItemServiceImpl implements ICartItemService {
    @Autowired
    private ICartItemRepository cartItemRepository;

    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private IUserRepository userRepository;

    @Override

    //Trả về tất cả các mục giỏ hàng mà không phân biệt người dùng.
    public List<CartItem> getCartItems() throws SimpleException {
        return cartItemRepository.findAll();
    }

    @Override
    public CartItem getCartItemById(Long cartItemId) throws SimpleException {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(()->new SimpleException("not exist"  + cartItemId, HttpStatus.NOT_FOUND));
    }

    @Override
    public CartItem saveCartItem(CartItem cartItem) throws SimpleException {
        Product product = cartItem.getProduct();
        Users user = cartItem.getUsers();

        // Kiểm tra nếu sản phẩm đã có trong giỏ hàng của người dùng
        CartItem existingCartItem = cartItemRepository.findByUsersAndProduct(user, product);

       if(existingCartItem != null) {
        // Cập nhật số lượng nếu sản phẩm đã có trong giỏ hàng
           int newQuantity = existingCartItem.getQuantity() + cartItem.getQuantity();
           if (newQuantity>product.getStock()) {
               throw new SimpleException("Quantity exceeds stock", HttpStatus.BAD_REQUEST);
           }
           existingCartItem.setQuantity(newQuantity);
           return cartItemRepository.save(existingCartItem);
       }else {
         // Kiểm tra số lượng trước khi thêm mới sản phẩm vào giỏ hàng
           if(cartItem.getQuantity()>product.getStock()) {
               throw new SimpleException("Quantity exceeds stock", HttpStatus.BAD_REQUEST);
           }
           cartItem.setProduct(product);
           cartItem.setUsers(user);
           return cartItemRepository.save(cartItem);
       }
    }

    @Override
    public CartItem updateCartItem(Long cartItemId, CartItem cartItem) throws SimpleException {
        // Kiểm tra sự tồn tại của mục giỏ hàng
        CartItem existingCartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new SimpleException("Cart item not found: " + cartItemId, HttpStatus.NOT_FOUND));

        // Kiểm tra sự tồn tại của sản phẩm
        Product product = cartItem.getProduct();
        if (product == null || !productRepository.existsById(product.getProductId())) {
            throw new SimpleException("Product not found", HttpStatus.NOT_FOUND);
        }

        // Kiểm tra số lượng trước khi update sản phẩm vào giỏ hàng
        if (cartItem.getQuantity()>product.getStock()) {
            throw new SimpleException("Quantity exceeds stock", HttpStatus.BAD_REQUEST);
        }
        // Cập nhật thông tin của mục giỏ hàng
        existingCartItem.setQuantity(cartItem.getQuantity());
        return cartItemRepository.save(existingCartItem);
    }

    @Override
    public void deleteCartItem(Long cartItemId) throws SimpleException {
        // Xóa mục giỏ hàng nếu tồn tại
        if (cartItemRepository.findById(cartItemId).isPresent()) {
            cartItemRepository.deleteById(cartItemId);
        } else {
            throw new SimpleException("Cart item not found: " + cartItemId, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void deleteAllCartItems(Long userId) {
        cartItemRepository.deleteByUsersUserId(userId);
    }


}
