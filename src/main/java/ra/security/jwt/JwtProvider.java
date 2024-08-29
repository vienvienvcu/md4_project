package ra.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

// lập trình AOP
@Slf4j
@Component
public class JwtProvider {
    
    @Value("${jwt.secret_key}")
    private String SECRET_KEY; // Khóa bí mật dùng để ký và xác thực JWT

    @Value("${jwt.expired}")
    private Long EXPIRED; // Thời gian hết hạn của JWT

    // Phương thức tạo JWT với tên người dùng
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Đặt chủ đề (subject) của JWT là tên người dùng
                .setIssuedAt(new Date()) // Đặt thời gian phát hành JWT
                .setExpiration(new Date(new Date().getTime() + EXPIRED)) // Đặt thời gian hết hạn
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY) // Ký JWT với thuật toán HS512 và khóa bí mật
                .compact(); // Tạo JWT dưới dạng chuỗi
    }

    // Phương thức kiểm tra JWT có hợp lệ không
    public Boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token); // Phân tích JWT với khóa bí mật
            return true; // JWT hợp lệ
        } catch (Exception e) {
            log.error("Exception {}", e.getMessage()); // Ghi lỗi nếu JWT không hợp lệ
        }
        return false; // JWT không hợp lệ
    }

    // Phương thức lấy tên người dùng từ JWT
    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject(); // Lấy chủ đề từ JWT
    }
}
