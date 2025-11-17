package com.hotel.management.service;

import com.hotel.management.entity.RestaurantTable;
import com.hotel.management.enums.TableStatus;
import com.hotel.management.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing restaurant tables
 */
@Service
@Transactional
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    /**
     * Get all available tables (status = AVAILABLE)
     * @return list of available tables
     * @throws RuntimeException if an unexpected error occurs
     */
    public List<RestaurantTable> getAvailableTables() {
        try {
            return tableRepository.findByStatus(TableStatus.AVAILABLE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get available tables: " + e.getMessage(), e);
        }
    }

    /**
     * Update the status of a table
     * @param tableId the ID of the table to update
     * @param newStatus the new status to set
     * @return the updated table
     * @throws IllegalArgumentException if table not found or invalid status
     * @throws RuntimeException if an unexpected error occurs
     */
    public RestaurantTable updateTableStatus(Long tableId, TableStatus newStatus) {
        try {
            if (tableId == null) {
                throw new IllegalArgumentException("Table ID cannot be null");
            }

            if (newStatus == null) {
                throw new IllegalArgumentException("New status cannot be null");
            }

            // Find the table
            Optional<RestaurantTable> tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isEmpty()) {
                throw new IllegalArgumentException("Table with ID " + tableId + " not found");
            }

            RestaurantTable table = tableOpt.get();
            TableStatus oldStatus = table.getStatus();

            // Update the status
            table.setStatus(newStatus);
            RestaurantTable updatedTable = tableRepository.save(table);

            return updatedTable;

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to update table status: " + e.getMessage(), e);
        }
    }

    /**
     * Get all tables
     * @return list of all tables
     */
    public List<RestaurantTable> getAllTables() {
        try {
            return tableRepository.findAllByOrderByTableNumberAsc();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all tables: " + e.getMessage(), e);
        }
    }

    /**
     * Get tables by status
     * @param status the status to filter by
     * @return list of tables with the specified status
     */
    public List<RestaurantTable> getTablesByStatus(TableStatus status) {
        try {
            if (status == null) {
                throw new IllegalArgumentException("Status cannot be null");
            }
            return tableRepository.findByStatus(status);
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to get tables by status: " + e.getMessage(), e);
        }
    }

    /**
     * Get a table by ID
     * @param id the table ID
     * @return the table if found
     */
    public Optional<RestaurantTable> getTableById(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Table ID cannot be null");
            }
            return tableRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get table by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Get a table by table number
     * @param tableNumber the table number
     * @return the table if found
     */
    public Optional<RestaurantTable> getTableByNumber(String tableNumber) {
        try {
            if (tableNumber == null || tableNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Table number cannot be null or empty");
            }
            return tableRepository.findByTableNumber(tableNumber);
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to get table by number: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new table
     * @param table the table to create
     * @return the saved table
     * @throws IllegalArgumentException if table number already exists or validation fails
     * @throws RuntimeException if an unexpected error occurs
     */
    public RestaurantTable createTable(RestaurantTable table) {
        try {
            if (table == null) {
                throw new IllegalArgumentException("Table cannot be null");
            }

            if (table.getTableNumber() == null || table.getTableNumber().trim().isEmpty()) {
                throw new IllegalArgumentException("Table number is required");
            }

            if (table.getCapacity() == null || table.getCapacity() < 1) {
                throw new IllegalArgumentException("Table capacity must be at least 1");
            }

            // Check if table number already exists
            if (tableRepository.existsByTableNumber(table.getTableNumber())) {
                throw new IllegalArgumentException("Table number '" + table.getTableNumber() + "' already exists");
            }

            // Set default status if not provided
            if (table.getStatus() == null) {
                table.setStatus(TableStatus.AVAILABLE);
            }

            return tableRepository.save(table);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to create table: " + e.getMessage(), e);
        }
    }

    /**
     * Get available tables with sufficient capacity for a party
     * @param partySize the size of the party
     * @return list of available tables that can accommodate the party
     */
    public List<RestaurantTable> getAvailableTablesForParty(Integer partySize) {
        try {
            if (partySize == null || partySize < 1) {
                throw new IllegalArgumentException("Party size must be at least 1");
            }
            return tableRepository.findAvailableTablesWithCapacity(partySize);
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to get available tables for party: " + e.getMessage(), e);
        }
    }

    /**
     * Count tables by status
     * @param status the status to count
     * @return number of tables with the specified status
     */
    public long countTablesByStatus(TableStatus status) {
        try {
            if (status == null) {
                throw new IllegalArgumentException("Status cannot be null");
            }
            return tableRepository.countByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to count tables by status: " + e.getMessage(), e);
        }
    }
}
