package ra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import ra.constans.RoleName;
import ra.security.exception.AccessDenied;
import ra.security.exception.JwtEntryPoint;
import ra.security.jwt.JwtTokenFilter;
import ra.security.principle.MyUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final MyUserDetailsService userDetailsService;
    private final JwtEntryPoint jwtEntryPoint;
    private final AccessDenied accessDenied;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF protection
                .authorizeHttpRequests(
                        url -> url
                                .requestMatchers("/api/v1/auth/**").permitAll() // Cho phép truy cập không cần xác thực cho các yêu cầu đến /api/v1/auth/**
                                .requestMatchers("/api/v1/admin/**").hasAuthority(RoleName.ROLE_ADMIN.toString()) // Yêu cầu quyền ADMIN cho các yêu cầu đến /api/v1/admin/**
                                .requestMatchers("/api/v1/manager/**").hasAuthority(RoleName.ROLE_MANAGER.toString()) // Yêu cầu quyền MANAGER cho các yêu cầu đến /api/v1/manager/**
                                .requestMatchers("/api/v1/user/**").hasAuthority(RoleName.ROLE_USER.toString()) // Yêu cầu quyền USER cho các yêu cầu đến /api/v1/user/**
                                .anyRequest().permitAll() // Cho phép tất cả các yêu cầu khác không được chỉ định ở trên
                )
                .authenticationProvider(authenticationProvider()) // Cấu hình provider cho xác thực
                .exceptionHandling(
                        exception -> exception
                                .authenticationEntryPoint(jwtEntryPoint) // Xử lý lỗi xác thực không thành công
                                .accessDeniedHandler(accessDenied) // Xử lý lỗi quyền truy cập bị từ chối
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Thiết lập chế độ quản lý phiên là STATLESS
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class) // Thêm bộ lọc JWT trước bộ lọc xác thực cơ bản
                .build(); // Xây dựng đối tượng SecurityFilterChain
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(); // Cung cấp bean PasswordEncoder sử dụng thuật toán BCrypt
    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // Tạo DaoAuthenticationProvider để xác thực dựa trên cơ sở dữ liệu
        provider.setPasswordEncoder(passwordEncoder()); // Sử dụng BCryptPasswordEncoder để mã hóa mật khẩu
        provider.setUserDetailsService(userDetailsService); // Cung cấp dịch vụ để tải thông tin người dùng
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration auth) throws Exception
    {
        return auth.getAuthenticationManager(); // Trả về AuthenticationManager từ cấu hình
    }
}
