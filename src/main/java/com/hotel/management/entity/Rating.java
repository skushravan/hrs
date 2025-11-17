package com.hotel.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

import com.hotel.management.enums.RatingStatus;

@Entity
@Table(name = "ratings")
public class Rating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Customer name is required")
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Email(message = "Please provide a valid email")
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Column(name = "rating", nullable = false)
    private Integer rating;
    
    @Size(max = 500, message = "Comment must not exceed 500 characters")
    @Column(name = "comment", length = 500)
    private String comment;
    
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RatingStatus status = RatingStatus.PENDING;
    
    // Constructors
    public Rating() {
        this.date = LocalDateTime.now();
    }
    
    public Rating(String customerName, String customerEmail, Integer rating, String comment) {
        this();
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    
    public RatingStatus getStatus() { return status; }
    public void setStatus(RatingStatus status) { this.status = status; }
}