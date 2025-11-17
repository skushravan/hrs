package com.hotel.management.repository;

import com.hotel.management.entity.Staff;
import com.hotel.management.enums.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Staff entity
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    /**
     * Find staff by email
     * @param email the email to search for
     * @return optional containing the staff if found
     */
    Optional<Staff> findByEmail(String email);

    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all staff by role
     * @param role the role to search for
     * @return list of staff with the specified role
     */
    List<Staff> findByRole(StaffRole role);

    /**
     * Find all staff by department
     * @param department the department to search for
     * @return list of staff in the specified department
     */
    List<Staff> findByDepartment(String department);

    /**
     * Find all active staff
     * @param isActive the active status
     * @return list of active/inactive staff
     */
    List<Staff> findByIsActive(Boolean isActive);

    /**
     * Find staff by role and active status
     * @param role the role to search for
     * @param isActive the active status
     * @return list of staff matching the criteria
     */
    List<Staff> findByRoleAndIsActive(StaffRole role, Boolean isActive);

    /**
     * Find staff by name (first name or last name contains the search term)
     * @param firstName the first name search term
     * @param lastName the last name search term
     * @return list of staff matching the name criteria
     */
    @Query("SELECT s FROM Staff s WHERE s.firstName LIKE %:firstName% OR s.lastName LIKE %:lastName%")
    List<Staff> findByNameContaining(@Param("firstName") String firstName, @Param("lastName") String lastName);

    /**
     * Find staff by full name
     * @param firstName the first name
     * @param lastName the last name
     * @return optional containing the staff if found
     */
    Optional<Staff> findByFirstNameAndLastName(String firstName, String lastName);

    /**
     * Count staff by role
     * @param role the role to count
     * @return number of staff with the specified role
     */
    long countByRole(StaffRole role);

    /**
     * Count staff by department
     * @param department the department to count
     * @return number of staff in the specified department
     */
    long countByDepartment(String department);

    /**
     * Count active staff
     * @param isActive the active status
     * @return number of active/inactive staff
     */
    long countByIsActive(Boolean isActive);

    /**
     * Find all staff ordered by last name, first name
     * @return list of all staff ordered by name
     */
    List<Staff> findAllByOrderByLastNameAscFirstNameAsc();

    /**
     * Find staff with tasks assigned
     * @return list of staff who have tasks assigned
     */
    @Query("SELECT DISTINCT s FROM Staff s JOIN s.assignedTasks t WHERE t.status != 'COMPLETED'")
    List<Staff> findStaffWithActiveTasks();

    /**
     * Find staff by role and department
     * @param role the role to search for
     * @param department the department to search for
     * @return list of staff matching both criteria
     */
    List<Staff> findByRoleAndDepartment(StaffRole role, String department);
}
