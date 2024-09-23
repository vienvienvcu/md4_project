package ra.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ra.exception.SimpleException;
import ra.model.dto.request.WishListRequest;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Product;
import ra.model.entity.Users;
import ra.model.entity.WishList;
import ra.repository.IProductRepository;
import ra.repository.IUserRepository;
import ra.repository.IWishListRepository;
import ra.security.principle.MyUserDetails;
import ra.service.IWishListService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class WishListController {
    @Autowired
    private IWishListService wishListService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private IWishListRepository wishListRepository;

    @GetMapping("/getAllWishList")
    public ResponseEntity<?> getAllWishList() throws SimpleException{
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Lấy tất cả các mục giỏ hàng của người dùng

        List<WishList> wishListList = wishListRepository.findByUsersUserId(userId);

        // Trả về danh sách các mục ds yeu thich

        return ResponseEntity.ok().body(new SimpleResponse(wishListList, HttpStatus.OK));

    }

    @PostMapping("/addWishList")
    public ResponseEntity<?> addWishList(@Valid @RequestBody WishListRequest wishListRequest) throws SimpleException{

        // Lấy thông tin người dùng từ SecurityContext
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        return ResponseEntity.ok()
                .body(new SimpleResponse(wishListService.saveWishList(userId,wishListRequest),HttpStatus.CREATED));

    }

    @DeleteMapping("/deleteWishList/{wishListId}")
    public ResponseEntity<?> deleteWishList (@PathVariable Integer wishListId) throws SimpleException{
        // Lấy thông tin người dùng từ SecurityContext
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();
        WishList wishList = wishListRepository.findById(wishListId)
                .orElseThrow(()->new SimpleException("UserId not found"+ userId,HttpStatus.NOT_FOUND));

        // Kiểm tra xem mục  YEU THICH có thuộc về người dùng hiện tại không

        if(!wishList.getUsers().getUserId().equals(userId)){
            throw new SimpleException("User not authorized", HttpStatus.FORBIDDEN);
        }

        //xoa muc yeu thich theo id

        wishListService.deleteWishList(wishListId);
        return ResponseEntity.ok().body(new SimpleResponse("WishList deleted successfully",HttpStatus.OK));

    }

    @DeleteMapping("/allDeleteWishList")
    public ResponseEntity<?> deleteAllWishList () throws SimpleException{

        // Lấy thông tin người dùng từ SecurityContext
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Xóa tất cả các mục yeu thich của người dùng
        wishListService.deleteAllWishList(userId);
        return ResponseEntity.ok().body(new SimpleResponse("All WishList deleted successfully",HttpStatus.OK));
    }


}
