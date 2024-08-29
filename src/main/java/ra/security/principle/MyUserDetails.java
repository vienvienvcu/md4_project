package ra.security.principle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ra.model.entity.Users;

import java.util.Collection;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MyUserDetails  implements UserDetails {
    // Thực thể người dùng chứa thông tin cơ bản của người dùng
    private Users users;
    // Danh sách các quyền của người dùng
    private Collection<? extends GrantedAuthority> authorities;

    // Trả về danh sách các quyền của người dùng
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    // Trả về mật khẩu của người dùng từ đối tượng Users
    @Override
    public String getPassword() {
        return this.getUsers().getPassword();
    }
    // Trả về tên người dùng (hoặc email) từ đối tượng Users
    @Override
    public String getUsername() {
        return this.getUsers().getEmail();
    }
    // Trả về true nếu tài khoản không hết hạn (trong trường hợp này, tài khoản luôn không hết hạn)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // Trả về true nếu tài khoản không bị khóa (trong trường hợp này, tài khoản luôn không bị khóa)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
// về true nếu thông tin đăng nhập (mật khẩu) không hết hạn (trong trường hợp này, thông tin luôn không hết hạn)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // Trả về true nếu tài khoản được kích hoạt (trong trường hợp này, tài khoản luôn được kích hoạt)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
