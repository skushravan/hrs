package com.hotel.management.repository;

import com.hotel.management.entity.InventoryItem;
import com.hotel.management.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByItemOrderByCreatedAtDesc(InventoryItem item);
}


