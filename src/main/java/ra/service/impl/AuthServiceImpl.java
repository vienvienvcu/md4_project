package ra.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ra.constans.RoleName;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.FormLogin;
import ra.model.dto.request.FormRegister;
import ra.model.dto.response.JwtResponse;
import ra.model.entity.Roles;
import ra.model.entity.Users;
import ra.repository.IRoleRepository;
import ra.repository.IUserRepository;
import ra.security.jwt.JwtProvider;
import ra.security.principle.MyUserDetails;
import ra.service.IAuthService;
import org.springframework.security.core.GrantedAuthority;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private final JwtProvider jwtProvider;

    @Override
    public void register(@RequestBody FormRegister formRegister) throws CustomException, SimpleException {
        // Kiểm tra tính duy nhất của email
        if (userRepository.findByEmail(formRegister.getEmail()).isPresent()) {
            throw new CustomException("Email already exists", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra tính duy nhất của phone
        if (userRepository.existsByPhone(formRegister.getPhone())) {
            throw new CustomException("Phone number already exists", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra tính duy nhất của username
        if (userRepository.findByUserName(formRegister.getUserName()).isPresent()) {
            throw new CustomException("Username already exists", HttpStatus.BAD_REQUEST);
        }

        Set<Roles> roles = new HashSet<>();
        roles.add(findByRoleName(RoleName.ROLE_USER));// Gán vai trò người dùng
        Users users = Users.builder()
                .fullName(formRegister.getFullName())
                .userName(formRegister.getUserName())
                .email(formRegister.getEmail())
                .password(passwordEncoder.encode(formRegister.getPassword()))// Mã hóa mật khẩu
                .phone(formRegister.getPhone())
                .roles(roles)
                .status(true)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        userRepository.save(users);// Lưu người dùng vào cơ sở dữ liệu
    }

    @Override
    public JwtResponse login(FormLogin formLogin) throws SimpleException {

        Authentication authentication;
        try
        {
            authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(formLogin.getEmail(), formLogin.getPassword()));
        }
        catch (AuthenticationException e)
        {
            throw new SimpleException("Username or password is incorrect", HttpStatus.BAD_REQUEST);
        }

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        if(!userDetails.getUsers().getStatus())
        {
            throw new SimpleException("Your account has blocked", HttpStatus.BAD_REQUEST);
        }

        return JwtResponse.builder()
                .accessToken(jwtProvider.generateToken(userDetails.getUsername()))
                .fullName(userDetails.getUsers().getFullName())
                .userName(userDetails.getUsers().getUserName())
                .email(userDetails.getUsers().getEmail())
                .phone(userDetails.getUsers().getPhone())
                .address(userDetails.getUsers().getAddress())
                .avatar(userDetails.getUsers().getAvatar())
                .createTime(userDetails.getUsers().getCreateTime())
                .updateTime(userDetails.getUsers().getUpdateTime())
                .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                .status(userDetails.getUsers().getStatus())
                .build();
    }

    public Roles findByRoleName(RoleName roleName) throws SimpleException
    {
        return roleRepository.findByRoleName(roleName).orElseThrow(() -> new SimpleException("Role not found", HttpStatus.NOT_FOUND));
    }

}

