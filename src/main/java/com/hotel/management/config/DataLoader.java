package com.hotel.management.config;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.InventoryItem;
import com.hotel.management.entity.InventoryTransaction;
import com.hotel.management.entity.RestaurantTable;
import com.hotel.management.entity.User;
import com.hotel.management.enums.ReservationStatus;
import com.hotel.management.enums.TableStatus;
import com.hotel.management.enums.UserRole;
import com.hotel.management.repository.UserRepository;
import com.hotel.management.service.ReservationService;
import com.hotel.management.service.TableService;
import com.hotel.management.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Event listener that runs after the application is ready
     * Loads sample data into the database
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadSampleData() {
        logger.info("Starting to load sample data...");
        
        try {
            // Load sample users (should be loaded first)
            loadSampleUsers();
            
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
     * Creates sample users with different roles for testing
     * 
     * Default password for all test users: password123
     * 
     * Test Users:
     * - admin / password123 (ROLE_ADMIN)
     * - manager / password123 (ROLE_MANAGER)
     * - receptionist / password123 (ROLE_RECEPTIONIST)
     * - staff / password123 (ROLE_STAFF)
     * - user / password123 (ROLE_USER)
     */
    private void loadSampleUsers() {
        logger.info("Loading sample users...");
        
        try {
            // Admin user
            createUserIfNotExists("admin", "password123", "admin@hotel.com", 
                "Admin", "User", Set.of(UserRole.ROLE_ADMIN));
            
            // Manager user
            createUserIfNotExists("manager", "password123", "manager@hotel.com", 
                "Manager", "User", Set.of(UserRole.ROLE_MANAGER));
            
            // Receptionist user
            createUserIfNotExists("receptionist", "password123", "receptionist@hotel.com", 
                "Receptionist", "User", Set.of(UserRole.ROLE_RECEPTIONIST));
            
            // Staff user
            createUserIfNotExists("staff", "password123", "staff@hotel.com", 
                "Staff", "User", Set.of(UserRole.ROLE_STAFF));
            
            // Regular user
            createUserIfNotExists("user", "password123", "user@hotel.com", 
                "Regular", "User", Set.of(UserRole.ROLE_USER));
            
            logger.info("Sample users loaded successfully!");
            logger.info("=== Test User Credentials ===");
            logger.info("Username: admin, Password: password123 (ROLE_ADMIN)");
            logger.info("Username: manager, Password: password123 (ROLE_MANAGER)");
            logger.info("Username: receptionist, Password: password123 (ROLE_RECEPTIONIST)");
            logger.info("Username: staff, Password: password123 (ROLE_STAFF)");
            logger.info("Username: user, Password: password123 (ROLE_USER)");
            logger.info("=============================");
            
        } catch (Exception e) {
            logger.error("Error creating sample users: {}", e.getMessage(), e);
        }
    }

    /**
     * Helper method to create a user if it doesn't already exist
     */
    private void createUserIfNotExists(String username, String password, String email, 
                                     String firstName, String lastName, Set<UserRole> roles) {
        try {
            if (!userRepository.existsByUsername(username)) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(passwordEncoder.encode(password)); // Hash the password
                user.setEmail(email);
                user.setFirstName(firstName);
                user.setLastName(lastName);
                user.setRoles(roles);
                user.setEnabled(true);
                user.setAccountNonExpired(true);
                user.setAccountNonLocked(true);
                user.setCredentialsNonExpired(true);
                
                userRepository.save(user);
                logger.info("Created user: {}", username);
            } else {
                logger.info("User {} already exists, skipping...", username);
            }
        } catch (Exception e) {
            logger.warn("Failed to create user {}: {}", username, e.getMessage());
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
                createReservation("Udaya Shetty", "+91 9876543210", availableTables.get(0), 
                    LocalDateTime.now().plusHours(2), 2, ReservationStatus.CONFIRMED),
                createReservation("Shivananda Bangera", "+91 9611693320", 
                    availableTables.size() > 1 ? availableTables.get(1) : availableTables.get(0), 
                    LocalDateTime.now().plusHours(3), 4, ReservationStatus.PENDING),
                createReservation("Santosh Naik", "+91 7795074320", 
                    availableTables.size() > 2 ? availableTables.get(2) : availableTables.get(0), 
                    LocalDateTime.now().plusDays(1), 6, ReservationStatus.CONFIRMED),
                createReservation("Gopal Achar", "+91 8105649828", 
                    availableTables.size() > 3 ? availableTables.get(3) : availableTables.get(0), 
                    LocalDateTime.now().plusDays(1).plusHours(2), 2, ReservationStatus.PENDING),
                createReservation("Vittal Kanchan", "+91 9900123456", 
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
