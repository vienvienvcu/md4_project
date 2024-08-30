package ra.constans;

public enum OrderStatus {
    WAITING,                // Đơn hàng mới chờ xác nhận/admin
    CONFIRM,                // Đã xác nhận/admin
    DELIVERY,               // Đang giao hàng/admin
    SUCCESS,               // Đã giao hàng/admin
    CANCEL,                // Đã hủy đơn  /user duoc dung
    DENIED                 // Bị từ chối/ admin
}
