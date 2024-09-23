package ra.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.response.DataResponse;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Users;
import ra.security.principle.MyUserDetails;
import ra.service.IUserService;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin")
public class UserManagerController {
    @Autowired
    private IUserService userService;


    // Lấy thông tin chi tiết của một người dùng theo ID
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) throws SimpleException {
        Users user = userService.getUserById(userId);
        return ResponseEntity.ok().body(new SimpleResponse(user, HttpStatus.OK));
    }


    // Lấy danh sách tất cả người dùng
    @GetMapping("/allUsers")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "asc")String sortDirection) throws CustomException {

        // Xác định hướng sắp xếp
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "userName"));

        // Lấy trang người dùng từ service
        Page<Users> userPage = userService.getAllUsers(pageable);

        // Tạo DataResponse và trả về
        return ResponseEntity.ok().body(new DataResponse<>(
                userPage.getContent(), // Nội dung trang
                HttpStatus.OK,
                userPage.getTotalPages(), // Tổng số trang
                userPage.getTotalElements(), // Tổng số phần tử
                userPage.getNumber(), // Trang hiện tại
                userPage.getNumberOfElements() // Số lượng phần tử trong trang hiện tại
        ));
    }

   // Tìm kiếm người dùng theo tên và phân trang

    @GetMapping("/searchSortAndPage")
    public ResponseEntity<?> getSearchUsers(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "asc") String sortDirection) throws CustomException {


        // Xác định hướng sắp xếp
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "userName"));
        Page<Users> userPage = userService.searchUsersByUsername(username, pageable);
        return ResponseEntity.ok().body(new DataResponse<>(
                userPage.getContent(), // Nội dung trang
                HttpStatus.OK,
                userPage.getTotalPages(), // Tổng số trang
                userPage.getTotalElements(), // Tổng số phần tử
                userPage.getNumber(), // Trang hiện tại
                userPage.getNumberOfElements() // Số lượng phần tử trong trang hiện tại
        ));
    }

    // Tìm kiếm người dùng theo tên

    @GetMapping("/searchByName")
    public ResponseEntity<?> getSearchUsers(
            @RequestParam String username) throws SimpleException{
        // Lấy trang người dùng từ service
        List<Users> userSearchList = userService.findUsersByUsername(username);
        // Tạo SimpleResponse và trả về
        return ResponseEntity.ok().body(new SimpleResponse(userSearchList, HttpStatus.OK));
    }


    //UPDATE STATUS OF USER

    @PutMapping("/updateUserStatus/{userId}")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Boolean status,
            @AuthenticationPrincipal UserDetails currentUser) throws SimpleException {

        // Lấy ID của người dùng hiện tại từ thông tin xác thực
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = userDetails.getUsers().getUserId();

        // Gọi phương thức dịch vụ để cập nhật trạng thái người dùng
        userService.updateUserByStatus(userId, status, currentUserId);

        // Trả về phản hồi thành công
        return ResponseEntity.ok().body(new SimpleResponse("Admin status updated successfully", HttpStatus.OK));
    }


}
