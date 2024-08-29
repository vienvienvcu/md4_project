package ra.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.FormRegister;
import ra.model.dto.response.DataResponse;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Users;
import ra.security.principle.MyUserDetails;
import ra.service.IUserService;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    @GetMapping
    public ResponseEntity<?> getFullName()
    {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(userDetails.getUsers().getUserName());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserDetails() {
        // Lấy thông tin người dùng từ SecurityContext
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Trả về toàn bộ thông tin của người dùng
        Users user = userDetails.getUsers();
        return ResponseEntity.ok().body(new SimpleResponse(user, HttpStatus.OK));
    }


    @PutMapping("/me")
    public ResponseEntity<?> updateUser(
            @ModelAttribute FormRegister updatedUserForm,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) throws  SimpleException {

        // Lấy ID của người dùng đang xác thực
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Gọi dịch vụ để cập nhật thông tin của người dùng đã xác thực
        Users updatedUser = userService.updateUser(userId, updatedUserForm, avatar);
        return ResponseEntity.ok().body(new SimpleResponse(updatedUser, HttpStatus.OK));
    }
}
