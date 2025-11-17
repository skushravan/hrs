package com.hotel.management.repository;

import com.hotel.management.entity.RestaurantTable;
import com.hotel.management.enums.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for RestaurantTable entity
 */
@Repository
public interface TableRepository extends JpaRepository<RestaurantTable, Long> {

    /**
     * Find all tables by status
     * @param status the table status to search for
     * @return list of tables with the specified status
     */
    List<RestaurantTable> findByStatus(TableStatus status);

    /**
     * Find a table by its table number
     * @param tableNumber the table number to search for
     * @return optional containing the table if found
     */
    Optional<RestaurantTable> findByTableNumber(String tableNumber);

    /**
     * Check if a table number exists
     * @param tableNumber the table number to check
     * @return true if the table number exists, false otherwise
     */
    boolean existsByTableNumber(String tableNumber);

    /**
     * Find tables by capacity (greater than or equal to specified capacity)
     * @param capacity the minimum capacity required
     * @return list of tables with capacity greater than or equal to specified capacity
     */
    List<RestaurantTable> findByCapacityGreaterThanEqual(Integer capacity);

    /**
     * Find available tables with sufficient capacity
     * @param capacity the minimum capacity required
     * @return list of available tables with sufficient capacity
     */
    @Query("SELECT t FROM RestaurantTable t WHERE t.status = 'AVAILABLE' AND t.capacity >= :capacity ORDER BY t.capacity ASC")
    List<RestaurantTable> findAvailableTablesWithCapacity(@Param("capacity") Integer capacity);

    /**
     * Find tables by status and capacity range
     * @param status the table status to search for
     * @param minCapacity the minimum capacity
     * @param maxCapacity the maximum capacity
     * @return list of tables matching the criteria
     */
    List<RestaurantTable> findByStatusAndCapacityBetween(TableStatus status, 
                                                        Integer minCapacity, 
                                                        Integer maxCapacity);

    /**
     * Count tables by status
     * @param status the table status to count
     * @return number of tables with the specified status
     */
    long countByStatus(TableStatus status);

    /**
     * Find all tables ordered by table number
     * @return list of all tables ordered by table number
     */
    List<RestaurantTable> findAllByOrderByTableNumberAsc();
}
