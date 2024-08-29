package ra.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String sku;
    private String productName;
    private String description;
    private Double price;
    private Integer stock;
    private String image;
    private Boolean status;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createTime;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date updateTime;
    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Categories category;
}
