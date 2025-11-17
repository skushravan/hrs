package com.hotel.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * JPA Entity representing an inventory transaction (stock in/out adjustments)
 */
@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {

    public enum Type { IN, OUT, ADJUSTMENT }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @NotNull(message = "Transaction type is required")
    private Type type;

    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(name = "note")
    @Size(max = 255, message = "Note must not exceed 255 characters")
    private String note;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    @Size(max = 100, message = "Created by must not exceed 100 characters")
    private String createdBy;

    public InventoryTransaction() {
        this.createdAt = LocalDateTime.now();
    }

    public InventoryTransaction(InventoryItem item, Type type, Integer quantity, String note, String createdBy) {
        this();
        this.item = item;
        this.type = type;
        this.quantity = quantity;
        this.note = note;
        this.createdBy = createdBy;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InventoryItem getItem() { return item; }
    public void setItem(InventoryItem item) { this.item = item; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}


