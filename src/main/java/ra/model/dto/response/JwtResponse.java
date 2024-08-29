package ra.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class JwtResponse {
    private String accessToken;
    private final String type = "Bearer";
    private String userName;
    private String fullName;
    private String email;
    private String phone;
    private Boolean status;
    private String avatar;
    private String address;
    private Date createTime;
    private Date updateTime;
    private Set<String> roles;
}
