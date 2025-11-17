package com.hotel.management.entity;

import com.hotel.management.enums.ReservationStatus;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * JPA Entity representing a restaurant reservation
 */
@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name", nullable = false)
    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;

    @Column(name = "customer_phone", nullable = false)
    @NotBlank(message = "Customer phone is required")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]{10,15}$", message = "Invalid phone number format")
    private String customerPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    @NotNull(message = "Table is required")
    private RestaurantTable table;

    @Column(name = "reservation_time", nullable = false)
    @NotNull(message = "Reservation time is required")
    private LocalDateTime reservationTime;

    @Column(name = "party_size", nullable = false)
    @NotNull(message = "Party size is required")
    @Min(value = 1, message = "Party size must be at least 1")
    @Max(value = 20, message = "Party size cannot exceed 20")
    private Integer partySize;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    private ReservationStatus status;

    // Default constructor
    public Reservation() {
        this.status = ReservationStatus.PENDING;
    }

    // Constructor with required fields
    public Reservation(String customerName, String customerPhone, RestaurantTable table, 
                      LocalDateTime reservationTime, Integer partySize) {
        this();
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.table = table;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
    }

    // Constructor with all fields
    public Reservation(String customerName, String customerPhone, RestaurantTable table, 
                      LocalDateTime reservationTime, Integer partySize, ReservationStatus status) {
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.table = table;
        this.reservationTime = reservationTime;
        this.partySize = partySize;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public RestaurantTable getTable() {
        return table;
    }

    public void setTable(RestaurantTable table) {
        this.table = table;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }

    public Integer getPartySize() {
        return partySize;
    }

    public void setPartySize(Integer partySize) {
        this.partySize = partySize;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", table=" + (table != null ? table.getTableNumber() : "null") +
                ", reservationTime=" + reservationTime +
                ", partySize=" + partySize +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
