package com.hotel.management.service;

import com.hotel.management.entity.InventoryItem;
import com.hotel.management.entity.InventoryTransaction;
import com.hotel.management.repository.InventoryItemRepository;
import com.hotel.management.repository.InventoryTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryItemRepository itemRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    public List<InventoryItem> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<InventoryItem> getItemById(Long id) {
        if (id == null) throw new IllegalArgumentException("Item ID cannot be null");
        return itemRepository.findById(id);
    }

    public InventoryItem createItem(InventoryItem item) {
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (itemRepository.existsByNameIgnoreCase(item.getName())) {
            throw new IllegalArgumentException("Item name '" + item.getName() + "' already exists");
        }
        if (item.getUnit() == null || item.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("Unit is required");
        }
        if (item.getQuantity() == null || item.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (item.getLowStockThreshold() == null || item.getLowStockThreshold() < 0) {
            throw new IllegalArgumentException("Low stock threshold cannot be negative");
        }
        return itemRepository.save(item);
    }

    public InventoryItem updateItem(Long id, InventoryItem updated) {
        if (id == null) throw new IllegalArgumentException("Item ID cannot be null");
        if (updated == null) throw new IllegalArgumentException("Updated item cannot be null");
        InventoryItem existing = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item with ID " + id + " not found"));

        if (updated.getName() != null && !updated.getName().equalsIgnoreCase(existing.getName())) {
            if (itemRepository.existsByNameIgnoreCase(updated.getName())) {
                throw new IllegalArgumentException("Item name '" + updated.getName() + "' already exists");
            }
            existing.setName(updated.getName());
        }
        if (updated.getCategory() != null) existing.setCategory(updated.getCategory());
        if (updated.getUnit() != null) existing.setUnit(updated.getUnit());
        if (updated.getLowStockThreshold() != null && updated.getLowStockThreshold() >= 0) {
            existing.setLowStockThreshold(updated.getLowStockThreshold());
        }
        // quantity should be modified through transactions
        return itemRepository.save(existing);
    }

    public void deleteItem(Long id) {
        if (id == null) throw new IllegalArgumentException("Item ID cannot be null");
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item with ID " + id + " not found");
        }
        itemRepository.deleteById(id);
    }

    public InventoryTransaction recordTransaction(Long itemId, InventoryTransaction.Type type, int quantity, String note, String createdBy) {
        if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
        if (type == null) throw new IllegalArgumentException("Transaction type is required");
        if (quantity < 1) throw new IllegalArgumentException("Quantity must be at least 1");

        InventoryItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item with ID " + itemId + " not found"));

        int newQty = item.getQuantity();
        switch (type) {
            case IN -> newQty += quantity;
            case OUT -> {
                if (item.getQuantity() - quantity < 0) {
                    throw new IllegalArgumentException("Insufficient stock for OUT transaction");
                }
                newQty -= quantity;
            }
            case ADJUSTMENT -> newQty = quantity; // absolute set
        }
        item.setQuantity(newQty);
        itemRepository.save(item);

        InventoryTransaction tx = new InventoryTransaction(item, type, quantity, note, createdBy);
        return transactionRepository.save(tx);
    }

    public List<InventoryItem> getLowStockItems() {
        return itemRepository.findLowStockItems();
    }

    public List<InventoryTransaction> getItemTransactions(Long itemId) {
        if (itemId == null) throw new IllegalArgumentException("Item ID cannot be null");
        InventoryItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item with ID " + itemId + " not found"));
        return transactionRepository.findByItemOrderByCreatedAtDesc(item);
    }
}


