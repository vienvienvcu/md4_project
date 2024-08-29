package ra.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@Slf4j // AOP
@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Ghi lại lỗi vào log khi có lỗi xác thực
        log.error("Un Authentication {}", authException.getMessage());

        // Thiết lập tiêu đề phản hồi để chỉ ra lỗi
        response.setHeader("error", "UNAUTHORIZED");

        // Đặt kiểu nội dung của phản hồi là JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Đặt mã trạng thái HTTP cho phản hồi là 401 (Chưa được xác thực)
        response.setStatus(401);

        // Tạo một bản đồ để chứa thông tin lỗi
        Map<String, Object> errors = new HashMap<>();
        errors.put("error", authException.getMessage());

        // Tạo đối tượng ObjectMapper để chuyển đổi bản đồ thành JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Viết nội dung lỗi dưới dạng JSON vào luồng phản hồi
        objectMapper.writeValue(response.getOutputStream(), errors);
    }
}
