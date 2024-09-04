package ra.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ra.exception.SimpleException;
import ra.model.dto.request.CartItemRequest;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.CartItem;
import ra.model.entity.Product;
import ra.model.entity.Users;
import ra.repository.ICartItemRepository;
import ra.repository.IProductRepository;
import ra.repository.IUserRepository;
import ra.security.principle.MyUserDetails;
import ra.service.ICartItemService;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class CartItemController {
    @Autowired
    private ICartItemService cartItemService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IProductRepository productRepository;

    @Autowired
    private ICartItemRepository cartItemRepository;

    @PostMapping("/addCart")
    public ResponseEntity<?> addProductToCart(@Valid @RequestBody CartItemRequest cartItemRequest) throws SimpleException {
        // Lấy thông tin người dùng từ SecurityContext
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Tìm người dùng và sản phẩm từ cơ sở dữ liệu
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new SimpleException("User not found", HttpStatus.NOT_FOUND));
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new SimpleException("Product not found", HttpStatus.NOT_FOUND));

        // Tạo đối tượng CartItem từ yêu cầu
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(cartItemRequest.getQuantity());
        cartItem.setUsers(user);
        cartItem.setProduct(product);

        CartItem savedCartItem = cartItemService.saveCartItem(cartItem);
        return ResponseEntity.ok().body(new SimpleResponse(savedCartItem, HttpStatus.CREATED));
    }

    @PutMapping("/updateCart/{cartItemId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartItemId, @Valid @RequestBody CartItemRequest cartItemRequest) throws SimpleException {
        // Lấy thông tin người dùng từ SecurityContext
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Tìm người dùng và sản phẩm từ cơ sở dữ liệu
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new SimpleException("User not found", HttpStatus.NOT_FOUND));
        Product product = productRepository.findById(cartItemRequest.getProductId())
                .orElseThrow(() -> new SimpleException("Product not found", HttpStatus.NOT_FOUND));
        // Tạo đối tượng CartItem từ yêu cầu
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(cartItemRequest.getQuantity());
        cartItem.setUsers(user);
        cartItem.setProduct(product);

        // Cập nhật sản phẩm trong giỏ hàng
        CartItem updatedCartItem = cartItemService.updateCartItem(cartItemId, cartItem);

        return ResponseEntity.ok().body(new SimpleResponse(updatedCartItem, HttpStatus.OK));
    }

    @GetMapping("/getAllCartItem")
    public ResponseEntity<?> getAllCartItem() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        // Lấy tất cả các mục giỏ hàng của người dùng
        List<CartItem> cartItems = cartItemRepository.findByUsersUserId(userId);
        // Trả về danh sách các mục giỏ hàng
        return ResponseEntity.ok().body(new SimpleResponse(cartItems, HttpStatus.OK));
    }


    @GetMapping("getCartById/{cartId}")
    public ResponseEntity<?> getCartById(@PathVariable Long cartId) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        // Tìm mục giỏ hàng theo ID
        CartItem cartItem = cartItemRepository.findById(cartId)
                .orElseThrow(() -> new SimpleException("Cart item not found", HttpStatus.NOT_FOUND));
        // Kiểm tra xem mục giỏ hàng có thuộc về người dùng hiện tại không
        if (!cartItem.getUsers().getUserId().equals(userId)) {
            throw new SimpleException("User not authorized", HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok().body(new SimpleResponse(cartItem, HttpStatus.OK));

    }

    @DeleteMapping("/deleteCart/{cartId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartId) throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        CartItem cartItem = cartItemRepository.findById(cartId)
                .orElseThrow(() -> new SimpleException("Cart item not found", HttpStatus.NOT_FOUND));

        // Kiểm tra xem mục giỏ hàng có thuộc về người dùng hiện tại không
        if (!cartItem.getUsers().getUserId().equals(userId)) {
            throw new SimpleException("User not authorized", HttpStatus.FORBIDDEN);
        }
        cartItemRepository.deleteById(cartId);
        return ResponseEntity.ok().body(new SimpleResponse("CartItem deleted successfully", HttpStatus.OK));
    }

    @DeleteMapping("/AllDeleteCart")
    public ResponseEntity<?> deleteAllCart() throws SimpleException {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
     // Xóa tất cả các mục giỏ hàng của người dùng
        cartItemService.deleteAllCartItems(userId);
        return  ResponseEntity.ok().body(new SimpleResponse("All CartItem deleted successfully", HttpStatus.OK));
    }
}
