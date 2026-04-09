package CNTT.DTO;

import jakarta.persistence.*;

@Entity
@Table(name = "finance")
public class FinanceDTO {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String type; // "THU" hoặc "CHI"
    private double amount;
    private String status; // "Chờ duyệt", "Đã duyệt", "Cảnh báo"
    private String note;

    // Getters và Setters
    public Long getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}