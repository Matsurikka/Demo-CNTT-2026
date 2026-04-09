package CNTT.DTO;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class EmployeeDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int experience;
    private double salary;
    private double bonus;
    private String rank;
    private int absentDays; 
    private double fine;

    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public double getBonus() { return bonus; }
    public void setBonus(double bonus) { this.bonus = bonus; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    public int getAbsentDays() { return absentDays; }
    public void setAbsentDays(int absentDays) { this.absentDays = absentDays; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine;  }
}