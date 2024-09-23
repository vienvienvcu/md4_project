package ra.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ra.exception.CustomException;
import ra.exception.SimpleException;
import ra.model.dto.request.FormLogin;
import ra.model.dto.request.FormRegister;
import ra.model.dto.response.SimpleResponse;
import ra.service.IAuthService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> handleRegister(@Valid @RequestBody FormRegister formRegister) throws CustomException, SimpleException {
        // Kiểm tra mật khẩu và xác nhận mật khẩu
        if (!formRegister.isPasswordMatching()) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }
        authService.register(formRegister);
        return ResponseEntity.created(URI.create("api/v1/auth/register"))
                .body(new SimpleResponse("Register Success ",HttpStatus.OK));
    }

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@Valid @RequestBody FormLogin formLogin) throws CustomException, SimpleException {
        return new ResponseEntity<>(authService.login(formLogin), HttpStatus.OK);
    }
}
