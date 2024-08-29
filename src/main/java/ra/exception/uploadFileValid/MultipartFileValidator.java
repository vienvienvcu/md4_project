package ra.exception.uploadFileValid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileValidator implements ConstraintValidator<ValidMultipartFile, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return false;
        }
        // Thêm kiểm tra khác nếu cần (ví dụ: kích thước file, loại file)
        return true;
    }

}
