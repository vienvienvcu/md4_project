package ra.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.FormRegister;
import ra.model.entity.Users;

import java.util.List;

public interface IUserService {
    Users updateUser(Long userId, FormRegister updatedUser, MultipartFile avatar) throws SimpleException;
    Users getUserById(Long userId) throws SimpleException;
    Page<Users> getAllUsers(Pageable pageable) throws CustomException;
    Page<Users> searchUsersByUsername(String username, Pageable pageable) throws CustomException;
    List<Users> findUsersByUsername(String username) throws SimpleException;
    void updateUserStatus(Long userId, Boolean status) throws SimpleException;
    void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) throws SimpleException;
    void updateUserByStatus(Long orderId, Boolean status) throws SimpleException;
}
