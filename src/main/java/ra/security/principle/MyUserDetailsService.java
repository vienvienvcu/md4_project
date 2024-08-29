package ra.security.principle;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ra.model.entity.Users;
import ra.repository.IUserRepository;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    // Khai báo repository để truy xuất thông tin người dùng từ cơ sở dữ liệu
    private final IUserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm người dùng theo tên người dùng (email)
        Users users = userRepository.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("User name " + username + " not found"));
        // Tạo và trả về đối tượng MyUserDetails chứa thông tin người dùng và quyền hạn
        return MyUserDetails.builder()
                .users(users)// Cung cấp đối tượng Users với thông tin người dùng
                      //        Chuyển đổi danh sách các vai trò của người dùng thành danh sách SimpleGrantedAuthority
                .authorities(users.getRoles().stream()
                        .map(roles -> new SimpleGrantedAuthority(roles.getRoleName()
                                .toString())).toList()//Chuyển đổi stream thành danh sách
                )
                .build();

    }
}
