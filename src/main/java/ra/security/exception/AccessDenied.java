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

@Slf4j
@Component
public class AccessDenied implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Ghi lại lỗi vào log khi người dùng bị từ chối quyền truy cập
        log.error("Un Permission {}", accessDeniedException.getMessage());

        // Thiết lập tiêu đề phản hồi để chỉ ra lỗi
        response.setHeader("error", "FORBIDEN");

        // Đặt kiểu nội dung của phản hồi là JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Đặt mã trạng thái HTTP cho phản hồi là 403 (Cấm truy cập)
        response.setStatus(403);

        // Tạo một bản đồ để chứa thông tin lỗi
        Map<String, Object> errors = new HashMap<>();
        errors.put("error", accessDeniedException.getMessage());

        // Tạo đối tượng ObjectMapper để chuyển đổi bản đồ thành JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Viết nội dung lỗi dưới dạng JSON vào luồng phản hồi
        objectMapper.writeValue(response.getOutputStream(), errors);
    }

}
