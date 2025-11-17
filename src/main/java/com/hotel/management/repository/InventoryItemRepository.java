package com.hotel.management.repository;

import com.hotel.management.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<InventoryItem> findByCategoryIgnoreCase(String category);

    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= i.lowStockThreshold")
    List<InventoryItem> findLowStockItems();
}


