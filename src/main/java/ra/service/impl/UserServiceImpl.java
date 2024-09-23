package ra.service.impl;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.FormRegister;
import ra.model.entity.Users;
import ra.repository.IUserRepository;
import ra.service.IUploadFile;
import ra.service.IUserService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IUploadFile uploadFile;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public Users updateUser(Long userId, FormRegister updatedUser, MultipartFile avatar) throws SimpleException {
        Users existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new SimpleException("User not found", HttpStatus.NOT_FOUND));

        // Cập nhật các trường nếu có thông tin
        if (updatedUser.getFullName() != null) {
            existingUser.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }

        // Cập nhật avatar nếu có
        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = uploadFile.uploadLocal(avatar);
            existingUser.setAvatar(avatarUrl);
        }

        existingUser.setUpdateTime(new Date()); // Cập nhật thời gian

        // Kiểm tra và cập nhật tên người dùng nếu cần
        if (updatedUser.getUserName() != null && !updatedUser.getUserName().equals(existingUser.getUserName())) {
            if (userRepository.findByUserName(updatedUser.getUserName()).isPresent()) {
                throw new SimpleException("User is existed", HttpStatus.BAD_REQUEST);
            }
            existingUser.setUserName(updatedUser.getUserName());
        }
        // Không cho phép cập nhật email
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            throw new SimpleException("Email cannot be updated", HttpStatus.BAD_REQUEST);
        }

        return userRepository.save(existingUser);
    }

    @Override
    public Users getUserById(Long userId) throws SimpleException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new SimpleException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public Page<Users> getAllUsers(Pageable pageable) throws CustomException {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<Users> searchUsersByUsername(String username, Pageable pageable) {
        return userRepository.findByUserNameContainingIgnoreCase(username, pageable);

    }

    @Override
    public List<Users> findUsersByUsername(String username) {
        return userRepository.findByUserNameContainingIgnoreCase(username);
    }

    @Transactional
    @Override
    public void updateUserStatus(Long userId, Boolean status) throws SimpleException {
        if (!userRepository.existsById(userId)) {
            throw new SimpleException("User not found", HttpStatus.NOT_FOUND);
        }else {
            userRepository.updateUsersByStatus(userId,status);
        }
    }

    @Override
    public void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) throws SimpleException {
        // Tìm người dùng hiện tại

        Users existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new SimpleException("User not found", HttpStatus.NOT_FOUND));

        // Kiểm tra mật khẩu hiện tại

        if (!passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
            throw new SimpleException("Wrong password", HttpStatus.BAD_REQUEST);
        }

        if (newPassword != null && !newPassword.equals(confirmPassword)) {
            throw new SimpleException("New password and confirmation do not match", HttpStatus.BAD_REQUEST);
        }
        // Mã hóa mật khẩu mới

        String encodedNewPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedNewPassword);

        // Cập nhật người dùng với mật khẩu mới
        userRepository.save(existingUser);

    }


    @Override
    public void updateUserByStatus(Long userId, Boolean status, Long currentUserId) throws SimpleException {
        // Tìm người dùng theo ID
        Optional<Users> existingUser = userRepository.findById(userId);
        if (!existingUser.isPresent()) {
            throw new SimpleException("User not found: " + userId, HttpStatus.NOT_FOUND);
        }
        Users userToUpdate = existingUser.get();

        // Kiểm tra vai trò của người dùng hiện tại

        userToUpdate.getRoles().forEach(role->{
            if ("ROLE_ADMIN".equals(role.getRoleName().toString())) {
                try {
                    throw new SimpleException("admin cannot block",HttpStatus.BAD_REQUEST);
                } catch (SimpleException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // Cập nhật trạng thái
        userToUpdate.setStatus(status);
        // Lưu lại đối tượng với trạng thái đã cập nhật
        userRepository.save(userToUpdate);
    }



}
