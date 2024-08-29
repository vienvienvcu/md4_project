package ra.model.dto.response;

import lombok.*;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
@Setter
@Builder
public class DataResponse<T> {
    private T content;
    private HttpStatus httpStatus;
    private int totalPages; // Tổng số trang
    private long totalElements; // Tổng số phần tử
    private int currentPage; // Trang hiện tại
    private int numberOfElements; // Số lượng phần tử trong trang hiện tại

    // Constructor cho phản hồi phân trang
    public DataResponse(T content, HttpStatus httpStatus, int totalPages, long totalElements,int currentPage,int numberOfElements) {
        this.content = content;
        this.httpStatus = httpStatus;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.numberOfElements = numberOfElements;

    }
    // Constructor cho phản hồi đơn giản khong phan trang
    public DataResponse(T content, HttpStatus httpStatus) {
        this.content = content;
        this.httpStatus = httpStatus;
    }
}
