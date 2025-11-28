package com.hotel.management.repository;

import com.hotel.management.entity.User;
import com.hotel.management.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     * @param username the username to search for
     * @return optional containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     * @param email the email to search for
     * @return optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if username exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find all enabled users
     * @param enabled the enabled status
     * @return list of users with the specified enabled status
     */
    List<User> findByEnabled(Boolean enabled);

    /**
     * Find users by role
     * @param role the role to search for
     * @return list of users with the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role")
    List<User> findByRole(@Param("role") UserRole role);

    /**
     * Find enabled users by role
     * @param role the role to search for
     * @param enabled the enabled status
     * @return list of enabled users with the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role AND u.enabled = :enabled")
    List<User> findByRoleAndEnabled(@Param("role") UserRole role, @Param("enabled") Boolean enabled);

    /**
     * Count users by enabled status
     * @param enabled the enabled status
     * @return number of users with the specified enabled status
     */
    long countByEnabled(Boolean enabled);

    /**
     * Find users by username or email containing the search term
     * @param searchTerm the search term
     * @return list of users matching the search criteria
     */
    @Query("SELECT u FROM User u WHERE u.username LIKE %:searchTerm% OR u.email LIKE %:searchTerm% OR u.firstName LIKE %:searchTerm% OR u.lastName LIKE %:searchTerm%")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
}

