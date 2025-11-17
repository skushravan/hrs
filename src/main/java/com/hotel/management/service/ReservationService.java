package com.hotel.management.service;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.RestaurantTable;
import com.hotel.management.enums.ReservationStatus;
import com.hotel.management.enums.TableStatus;
import com.hotel.management.repository.ReservationRepository;
import com.hotel.management.repository.TableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing restaurant reservations
 */
@Service
@Transactional
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    /**
     * Create a new reservation after checking table availability
     * @param reservation the reservation to create
     * @return the saved reservation
     * @throws IllegalArgumentException if table is not available or validation fails
     * @throws RuntimeException if an unexpected error occurs
     */
    public Reservation createReservation(Reservation reservation) {
        try {
            // Validate input
            if (reservation == null) {
                throw new IllegalArgumentException("Reservation cannot be null");
            }

            if (reservation.getTable() == null || reservation.getTable().getId() == null) {
                throw new IllegalArgumentException("Table must be specified for reservation");
            }

            // Check if table exists and is available
            Optional<RestaurantTable> tableOpt = tableRepository.findById(reservation.getTable().getId());
            if (tableOpt.isEmpty()) {
                throw new IllegalArgumentException("Table with ID " + reservation.getTable().getId() + " not found");
            }

            RestaurantTable table = tableOpt.get();
            if (table.getStatus() != TableStatus.AVAILABLE) {
                throw new IllegalArgumentException("Table " + table.getTableNumber() + " is not available. Current status: " + table.getStatus());
            }

            // Check if party size exceeds table capacity
            if (reservation.getPartySize() > table.getCapacity()) {
                throw new IllegalArgumentException("Party size (" + reservation.getPartySize() + 
                    ") exceeds table capacity (" + table.getCapacity() + ")");
            }

            // Check if reservation time is in the future
            if (reservation.getReservationTime().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Reservation time must be in the future");
            }

            // Set default status if not provided
            if (reservation.getStatus() == null) {
                reservation.setStatus(ReservationStatus.PENDING);
            }

            // Save the reservation
            Reservation savedReservation = reservationRepository.save(reservation);

            // Update table status to RESERVED
            table.setStatus(TableStatus.RESERVED);
            tableRepository.save(table);

            return savedReservation;

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to create reservation: " + e.getMessage(), e);
        }
    }

    /**
     * Get all reservations for a specific date
     * @param date the date to get reservations for
     * @return list of reservations for the specified date
     * @throws IllegalArgumentException if date is null
     * @throws RuntimeException if an unexpected error occurs
     */
    public List<Reservation> getReservationsForDate(LocalDate date) {
        try {
            if (date == null) {
                throw new IllegalArgumentException("Date cannot be null");
            }

            // Create start and end of day
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            return reservationRepository.findByReservationTimeBetween(startOfDay, endOfDay);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to get reservations for date " + date + ": " + e.getMessage(), e);
        }
    }

    /**
     * Update the status of a reservation
     * @param reservationId the ID of the reservation to update
     * @param newStatus the new status to set
     * @return the updated reservation
     * @throws IllegalArgumentException if reservation not found or invalid status
     * @throws RuntimeException if an unexpected error occurs
     */
   

    /**
     * Get all reservations by status
     * @param status the status to filter by (null returns all reservations)
     * @return list of reservations with the specified status, or all reservations if status is null
     */
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        try {
            if (status == null) {
                return reservationRepository.findAll();
            }
            return reservationRepository.findByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get reservations by status: " + e.getMessage(), e);
        }
    }

    /**
     * Get a reservation by ID
     * @param id the reservation ID
     * @return the reservation if found
     */
    public Optional<Reservation> getReservationById(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Reservation ID cannot be null");
            }
            return reservationRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get reservation by ID: " + e.getMessage(), e);
        }
    }
    // End of ReservationService class
 
public List<Reservation> getAllReservations() {
    try {
        return reservationRepository.findAll();
    } catch (Exception e) {
        throw new RuntimeException("Failed to get all reservations: " + e.getMessage(), e);
    }
}

/**
 * Cancel a reservation by ID
 * @param reservationId the ID of the reservation to cancel
 * @return the cancelled reservation
 * @throws IllegalArgumentException if reservation not found
 */
public Reservation cancelReservation(Long reservationId) {
    try {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        // Find reservation
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation with ID " + reservationId + " not found"));

        logger.info("Processing reservation {} - current status: {}, new status: {}", 
            reservationId, reservation.getStatus(), ReservationStatus.CANCELLED);

        // Get fresh table instance
        RestaurantTable table = tableRepository.findById(reservation.getTable().getId())
            .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        // Update reservation status to CANCELLED
        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation cancelledReservation = reservationRepository.save(reservation);

        // Free up the table
        table.setStatus(TableStatus.AVAILABLE);
        tableRepository.save(table);

        return cancelledReservation;

    } catch (IllegalArgumentException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException("Failed to cancel reservation: " + e.getMessage(), e);
    }
}

        /**
 * Update the status of a reservation with table management
 */
public Reservation updateReservationStatus(Long reservationId, ReservationStatus newStatus) {
    try {
        if (reservationId == null) {
            throw new IllegalArgumentException("Reservation ID cannot be null");
        }

        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }

        // Find reservation within transaction
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new IllegalArgumentException("Reservation with ID " + reservationId + " not found"));

        logger.info("Processing reservation {} - current status: {}, new status: {}", 
            reservationId, reservation.getStatus(), newStatus);

        // Get fresh table instance to avoid detached entity issues
        RestaurantTable table = tableRepository.findById(reservation.getTable().getId())
            .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        // Update reservation status
        reservation.setStatus(newStatus);
        Reservation updatedReservation = reservationRepository.save(reservation);

        // Update table status
        switch (newStatus) {
            case CONFIRMED:
            case PENDING:
                table.setStatus(TableStatus.RESERVED);
                break;
            case SEATED:
            case IN_SERVICE:
                table.setStatus(TableStatus.OCCUPIED);
                break;
            case COMPLETED:
            case CANCELLED:
                table.setStatus(TableStatus.AVAILABLE);
                break;
        }
        
        tableRepository.save(table);

        return updatedReservation;

    } catch (IllegalArgumentException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException("Failed to update reservation status: " + e.getMessage(), e);
    }
}

}