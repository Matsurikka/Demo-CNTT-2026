package CNTT.DTO;

import jakarta.persistence.*;

@Entity
@Table(name = "production")
public class ProductionDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_name")
    private String productName;
    private int quantity;    // Tổng số lượng sản xuất
    private int defects;     // Số sản phẩm lỗi
    @Column(name = "error_rate")
    private double errorRate; // Tỷ lệ lỗi (Hệ chuyên gia tính)
    private String status;    // Kết quả kiểm tra (ĐẠT / HUỶ LÔ / CẢNH BÁO)
    //Constructor mặc định cho Hibernate
    public ProductionDTO() {}
    // Getter và Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getDefects() { return defects; }
    public void setDefects(int defects) { this.defects = defects; }
    public double getErrorRate() { return errorRate; }
    public void setErrorRate(double errorRate) { this.errorRate = errorRate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}