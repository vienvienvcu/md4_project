package ra.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.ChangePasswordRequest;
import ra.model.dto.request.FormRegister;
import ra.model.dto.response.DataResponse;
import ra.model.dto.response.SimpleResponse;
import ra.model.entity.Users;
import ra.security.principle.MyUserDetails;
import ra.service.IUserService;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class
UserController {
    private final IUserService userService;
    @GetMapping
    public ResponseEntity<?> getFullName()
    {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(userDetails.getUsers().getUserName());
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<?> getUserDetails() {
        // Lấy thông tin người dùng từ SecurityContext
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Trả về toàn bộ thông tin của người dùng
        Users user = userDetails.getUsers();
        return ResponseEntity.ok().body(new SimpleResponse(user, HttpStatus.OK));
    }


    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(
            @ModelAttribute FormRegister updatedUserForm,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) throws  SimpleException {

        // Lấy ID của người dùng đang xác thực
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();


        Users updatedUser = userService.updateUser(userId, updatedUserForm, avatar);
        return ResponseEntity.ok().body(new SimpleResponse("Update successful", HttpStatus.OK));
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) throws SimpleException {

        // Lấy ID của người dùng đang xác thực
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = userDetails.getUsers().getUserId();

        // Gọi dịch vụ để thay đổi mật khẩu
        userService.changePassword(userId, changePasswordRequest.getCurrentPassword(),
                changePasswordRequest.getNewPassword(),changePasswordRequest.getConfirmPassword());

        return ResponseEntity.ok().body(new SimpleResponse("Password changed successfully", HttpStatus.OK));
    }
}
