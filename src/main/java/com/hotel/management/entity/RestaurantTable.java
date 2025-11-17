package com.hotel.management.entity;

import com.hotel.management.enums.TableStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * JPA Entity representing a restaurant table
 */
@Entity
@Table(name = "tables")
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", unique = true, nullable = false)
    @NotBlank(message = "Table number is required")
    private String tableNumber;

    @Column(name = "capacity", nullable = false)
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    private TableStatus status;

    // Default constructor
    public RestaurantTable() {
        this.status = TableStatus.AVAILABLE;
    }

    // Constructor with required fields
    public RestaurantTable(String tableNumber, Integer capacity) {
        this();
        this.tableNumber = tableNumber;
        this.capacity = capacity;
    }

    // Constructor with all fields
    public RestaurantTable(String tableNumber, Integer capacity, TableStatus status) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RestaurantTable{" +
                "id=" + id +
                ", tableNumber='" + tableNumber + '\'' +
                ", capacity=" + capacity +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantTable that = (RestaurantTable) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
