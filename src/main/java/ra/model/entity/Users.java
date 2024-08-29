package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String userName;
    private String fullName;
    private String email;
    private String phone;
    @JsonIgnore
    private String password;
    private String avatar;
    private String address;
    private Boolean status; // True = ACTIVE, False = BLOCK
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createTime;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date updateTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Roles> roles;

}
