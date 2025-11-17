package com.hotel.management.config;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.InventoryItem;
import com.hotel.management.entity.InventoryTransaction;
import com.hotel.management.entity.RestaurantTable;
import com.hotel.management.enums.ReservationStatus;
import com.hotel.management.enums.TableStatus;
import com.hotel.management.service.ReservationService;
import com.hotel.management.service.TableService;
import com.hotel.management.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * DataLoader component to pre-populate the database with sample data
 * 
 * This component runs after the application startup and creates initial
 * sample data including restaurant tables and reservations for testing
 * and demonstration purposes.
 */
@Component
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Autowired
    private TableService tableService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private InventoryService inventoryService;

    /**
     * Event listener that runs after the application is ready
     * Loads sample data into the database
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadSampleData() {
        logger.info("Starting to load sample data...");
        
        try {
            // Load sample tables
            loadSampleTables();
            
            // Load sample reservations
            loadSampleReservations();

            // Load sample inventory
            loadSampleInventory();
            
            logger.info("Sample data loaded successfully!");
            
        } catch (Exception e) {
            logger.error("Error loading sample data: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates sample restaurant tables
     */
    private void loadSampleTables() {
        logger.info("Loading sample restaurant tables...");
        
        List<RestaurantTable> sampleTables = Arrays.asList(
            createTable("T01", 2, TableStatus.AVAILABLE),
            createTable("T02", 4, TableStatus.AVAILABLE),
            createTable("T03", 6, TableStatus.AVAILABLE),
            createTable("T04", 2, TableStatus.AVAILABLE),
            createTable("T05", 4, TableStatus.AVAILABLE),
            createTable("T06", 8, TableStatus.AVAILABLE),
            createTable("T07", 2, TableStatus.AVAILABLE),
            createTable("T08", 4, TableStatus.AVAILABLE),
            createTable("T09", 6, TableStatus.AVAILABLE),
            createTable("T10", 10, TableStatus.AVAILABLE)
        );

        for (RestaurantTable table : sampleTables) {
            try {
                // Check if table already exists
                var existingTable = tableService.getTableByNumber(table.getTableNumber());
                if (existingTable.isEmpty()) {
                    tableService.createTable(table);
                    logger.info("Created table: {}", table.getTableNumber());
                } else {
                    logger.info("Table {} already exists, skipping...", table.getTableNumber());
                }
            } catch (Exception e) {
                logger.warn("Failed to create table {}: {}", table.getTableNumber(), e.getMessage());
            }
        }
    }

    /**
     * Creates sample reservations
     */
    private void loadSampleReservations() {
        logger.info("Loading sample reservations...");
        
        try {
            // Get available tables for reservations
            var availableTables = tableService.getAvailableTables();
            if (availableTables.isEmpty()) {
                logger.warn("No available tables found for creating sample reservations");
                return;
            }

            // Create sample reservations with proper bounds checking
            List<Reservation> sampleReservations = Arrays.asList(
                createReservation("John Smith", "+1-555-0101", availableTables.get(0), 
                    LocalDateTime.now().plusHours(2), 2, ReservationStatus.CONFIRMED),
                createReservation("Sarah Johnson", "+1-555-0102", 
                    availableTables.size() > 1 ? availableTables.get(1) : availableTables.get(0), 
                    LocalDateTime.now().plusHours(3), 4, ReservationStatus.PENDING),
                createReservation("Mike Davis", "+1-555-0103", 
                    availableTables.size() > 2 ? availableTables.get(2) : availableTables.get(0), 
                    LocalDateTime.now().plusDays(1), 6, ReservationStatus.CONFIRMED),
                createReservation("Emily Wilson", "+1-555-0104", 
                    availableTables.size() > 3 ? availableTables.get(3) : availableTables.get(0), 
                    LocalDateTime.now().plusDays(1).plusHours(2), 2, ReservationStatus.PENDING),
                createReservation("David Brown", "+1-555-0105", 
                    availableTables.size() > 4 ? availableTables.get(4) : availableTables.get(0), 
                    LocalDateTime.now().plusDays(2), 4, ReservationStatus.CONFIRMED)
            );

            for (Reservation reservation : sampleReservations) {
                try {
                    reservationService.createReservation(reservation);
                    logger.info("Created reservation for: {}", reservation.getCustomerName());
                } catch (Exception e) {
                    logger.warn("Failed to create reservation for {}: {}", 
                        reservation.getCustomerName(), e.getMessage());
                }
            }
            
        } catch (Exception e) {
            logger.error("Error creating sample reservations: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates sample inventory items and a few transactions
     */
    private void loadSampleInventory() {
        logger.info("Loading sample inventory...");
        try {
            List<InventoryItem> items = Arrays.asList(
                new InventoryItem("Tomatoes", "Produce", "kg", 20, 5),
                new InventoryItem("Olive Oil", "Pantry", "l", 10, 2),
                new InventoryItem("Napkins", "Supplies", "pcs", 500, 100),
                new InventoryItem("Chicken Breast", "Meat", "kg", 15, 5)
            );

            for (InventoryItem item : items) {
                try {
                    // create if not exists
                    boolean exists = inventoryService.getAllItems().stream()
                        .anyMatch(i -> i.getName().equalsIgnoreCase(item.getName()));
                    if (!exists) {
                        InventoryItem saved = inventoryService.createItem(item);
                        logger.info("Created inventory item: {}", saved.getName());
                    }
                } catch (Exception e) {
                    logger.warn("Failed to create inventory item {}: {}", item.getName(), e.getMessage());
                }
            }

            // record a couple of transactions
            try {
                var tomatoes = inventoryService.getAllItems().stream()
                    .filter(i -> i.getName().equalsIgnoreCase("Tomatoes")).findFirst();
                tomatoes.ifPresent(i -> {
                    inventoryService.recordTransaction(i.getId(), InventoryTransaction.Type.OUT, 3, "Lunch prep", "system");
                    inventoryService.recordTransaction(i.getId(), InventoryTransaction.Type.IN, 10, "Supplier delivery", "system");
                });
            } catch (Exception e) {
                logger.warn("Failed to record inventory transactions: {}", e.getMessage());
            }

        } catch (Exception e) {
            logger.error("Error creating sample inventory: {}", e.getMessage(), e);
        }
    }

    /**
     * Helper method to create a restaurant table
     */
    private RestaurantTable createTable(String tableNumber, Integer capacity, TableStatus status) {
        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(tableNumber);
        table.setCapacity(capacity);
        table.setStatus(status);
        return table;
    }

    /**
     * Helper method to create a reservation
     */
    private Reservation createReservation(String customerName, String customerPhone, 
                                        RestaurantTable table, LocalDateTime reservationTime, 
                                        Integer partySize, ReservationStatus status) {
        Reservation reservation = new Reservation();
        reservation.setCustomerName(customerName);
        reservation.setCustomerPhone(customerPhone);
        reservation.setTable(table);
        reservation.setReservationTime(reservationTime);
        reservation.setPartySize(partySize);
        reservation.setStatus(status);
        return reservation;
    }
}
