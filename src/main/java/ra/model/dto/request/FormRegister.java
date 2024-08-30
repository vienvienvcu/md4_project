package ra.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FormRegister {
    @NotBlank(message = "Full name cannot be empty")
    private String fullName;

    @NotBlank(message = "User name cannot be empty")
    @Column(unique = true)
    private String userName;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "^(?:\\+84|0)[1-9][0-9]{8,9}$", message = "Invalid phone number format")
    @Column(unique = true)
    private String phone;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm password cannot be empty")
    private String confirmPassword;

    private MultipartFile avatar;

    private String address;

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
}
