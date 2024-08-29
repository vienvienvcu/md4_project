package ra.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ra.security.principle.MyUserDetailsService;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    // Đối tượng cung cấp các phương thức liên quan đến JWT
    private final JwtProvider jwtProvider;
    // Dịch vụ để tải thông tin người dùng
    private final MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/**
 * header {
 *    Authorization: Bearer asdasjkldaskljdkflas3213u129asdasdjklas
 *    'Content_Type': 'application/json' / 'multipart/form-data'
 * }
 * */
        try
        {
            String token = getTokenFromRequest(request); // Lấy token từ header của yêu cầu
            if (token != null && jwtProvider.validateToken(token)) // Kiểm tra tính hợp lệ của token
            {
                // Lấy tên người dùng từ token
                String username = jwtProvider.getUsernameFromToken(token);
                // Tải thông tin người dùng từ tên người dùng
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // Tạo đối tượng Authentication
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                // Cập nhật SecurityContext với Authentication
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch (Exception e)
        {
            log.error("Exception {}", e.getMessage());// Ghi lỗi nếu có ngoại lệ xảy ra
        }
        filterChain.doFilter(request,response);// Tiếp tục xử lý chuỗi bộ lọc
    }

    private String getTokenFromRequest(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization"); // Lấy giá trị của header Authorization
        if (header != null && header.startsWith("Bearer "))// Kiểm tra xem header có bắt đầu với "Bearer "
        {
            return header.substring(7);// Trích xuất token (bỏ qua phần "Bearer ")
        }
        return null; // Trả về null nếu không tìm thấy token
    }

}
