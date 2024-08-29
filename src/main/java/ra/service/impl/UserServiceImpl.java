package ra.service.impl;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IUploadFile uploadFile;

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


}
