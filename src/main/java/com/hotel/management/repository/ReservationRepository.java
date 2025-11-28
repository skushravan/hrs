package com.hotel.management.repository;

import com.hotel.management.entity.Reservation;
import com.hotel.management.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data JPA Repository for Reservation entity
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Find all reservations by status
     * @param status the reservation status to search for
     * @return list of reservations with the specified status
     */
    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * Find all reservations within a date range
     * @param startTime the start of the time range (inclusive)
     * @param endTime the end of the time range (inclusive)
     * @return list of reservations within the specified time range
     */
    List<Reservation> findByReservationTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find reservations that are not in a specific status
     * @param status status that should be excluded
     * @return list of reservations without the provided status
     */
    List<Reservation> findByStatusNot(ReservationStatus status);

    /**
     * Find reservations by status and within a date range
     * @param status the reservation status to search for
     * @param startTime the start of the time range (inclusive)
     * @param endTime the end of the time range (inclusive)
     * @return list of reservations with specified status within the time range
     */
    List<Reservation> findByStatusAndReservationTimeBetween(ReservationStatus status, 
                                                           LocalDateTime startTime, 
                                                           LocalDateTime endTime);

    /**
     * Find reservations by customer name (case-insensitive)
     * @param customerName the customer name to search for
     * @return list of reservations for the specified customer
     */
    List<Reservation> findByCustomerNameIgnoreCase(String customerName);

    /**
     * Find reservations by customer phone number
     * @param customerPhone the customer phone number to search for
     * @return list of reservations for the specified phone number
     */
    List<Reservation> findByCustomerPhone(String customerPhone);

    /**
     * Find reservations by table and status
     * @param tableId the table ID to search for
     * @param status the reservation status to search for
     * @return list of reservations for the specified table and status
     */
    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId AND r.status = :status")
    List<Reservation> findByTableIdAndStatus(@Param("tableId") Long tableId, 
                                           @Param("status") ReservationStatus status);

    /**
     * Count reservations by status
     * @param status the reservation status to count
     * @return number of reservations with the specified status
     */
    long countByStatus(ReservationStatus status);
}
