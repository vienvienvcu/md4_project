package ra.model.entity;

import jakarta.persistence.*;
import lombok.*;
import ra.constans.RoleName;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    @Enumerated(EnumType.STRING)
    private RoleName roleName;
}
