package ra.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductRequest {
    private String sku;
    @NotBlank(message = "Product name is cannot empty")
    @Column(unique = true)
    private String productName;

    @NotBlank(message = "Product description is cannot empty")
    private String description;

    @NotNull(message = "Product price is cannot empty")
    private Double price;

    @NotNull(message = "Product stock is cannot empty")
    private Integer stock;

    private MultipartFile image;
    @NotNull(message = "Category Id is cannot empty")
    private Long categoryId;
    @NotNull(message = "Product Status is cannot empty")
    private Boolean status;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createTime;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date updateTime;
}
