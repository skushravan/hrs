package com.hotel.management.service;

import com.hotel.management.entity.RestaurantTable;
import com.hotel.management.entity.Staff;
import com.hotel.management.enums.StaffRole;
import com.hotel.management.repository.StaffRepository;
import com.hotel.management.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing staff members
 */
@Service
@Transactional
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private TableRepository tableRepository;

    /**
     * Create a new staff member
     * @param staff the staff to create
     * @return the saved staff
     * @throws IllegalArgumentException if email already exists or validation fails
     * @throws RuntimeException if an unexpected error occurs
     */
    public Staff createStaff(Staff staff) {
        try {
            if (staff == null) {
                throw new IllegalArgumentException("Staff cannot be null");
            }

            if (staff.getEmail() == null || staff.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Email is required");
            }

            // Check if email already exists
            if (staffRepository.existsByEmail(staff.getEmail())) {
                throw new IllegalArgumentException("Email '" + staff.getEmail() + "' already exists");
            }

            // Set default values if not provided
            if (staff.getIsActive() == null) {
                staff.setIsActive(true);
            }

            if (staff.getHireDate() == null) {
                staff.setHireDate(LocalDate.now());
            }

            return staffRepository.save(staff);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to create staff: " + e.getMessage(), e);
        }
    }

    /**
     * Get all staff members
     * @return list of all staff members
     */
    public List<Staff> getAllStaff() {
        try {
            List<Staff> staff = staffRepository.findAllByOrderByLastNameAscFirstNameAsc();
            // Initialize assigned tables to avoid lazy loading issues
            staff.forEach(s -> {
                if (s.getAssignedTables() != null) {
                    s.getAssignedTables().size(); // Force initialization
                }
            });
            return staff;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get all staff: " + e.getMessage(), e);
        }
    }

    /**
     * Get staff by ID
     * @param id the staff ID
     * @return the staff if found
     */
    public Optional<Staff> getStaffById(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }
            return staffRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get staff by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Get staff by email
     * @param email the email address
     * @return the staff if found
     */
    public Optional<Staff> getStaffByEmail(String email) {
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            return staffRepository.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get staff by email: " + e.getMessage(), e);
        }
    }

    /**
     * Get staff by role
     * @param role the staff role
     * @return list of staff with the specified role
     */
    public List<Staff> getStaffByRole(StaffRole role) {
        try {
            if (role == null) {
                throw new IllegalArgumentException("Role cannot be null");
            }
            return staffRepository.findByRole(role);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get staff by role: " + e.getMessage(), e);
        }
    }

    /**
     * Get staff by department
     * @param department the department
     * @return list of staff in the specified department
     */
    public List<Staff> getStaffByDepartment(String department) {
        try {
            if (department == null || department.trim().isEmpty()) {
                throw new IllegalArgumentException("Department cannot be null or empty");
            }
            return staffRepository.findByDepartment(department);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get staff by department: " + e.getMessage(), e);
        }
    }

    /**
     * Get active staff members
     * @return list of active staff members
     */
    public List<Staff> getActiveStaff() {
        try {
            return staffRepository.findByIsActive(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get active staff: " + e.getMessage(), e);
        }
    }

    /**
     * Update staff member
     * @param id the staff ID
     * @param updatedStaff the updated staff data
     * @return the updated staff
     * @throws IllegalArgumentException if staff not found or validation fails
     * @throws RuntimeException if an unexpected error occurs
     */
    public Staff updateStaff(Long id, Staff updatedStaff) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }

            if (updatedStaff == null) {
                throw new IllegalArgumentException("Updated staff data cannot be null");
            }

            // Find existing staff
            Optional<Staff> existingStaffOpt = staffRepository.findById(id);
            if (existingStaffOpt.isEmpty()) {
                throw new IllegalArgumentException("Staff with ID " + id + " not found");
            }

            Staff existingStaff = existingStaffOpt.get();

            // Update fields
            if (updatedStaff.getFirstName() != null) {
                existingStaff.setFirstName(updatedStaff.getFirstName());
            }
            if (updatedStaff.getLastName() != null) {
                existingStaff.setLastName(updatedStaff.getLastName());
            }
            if (updatedStaff.getEmail() != null && !updatedStaff.getEmail().equals(existingStaff.getEmail())) {
                // Check if new email already exists
                if (staffRepository.existsByEmail(updatedStaff.getEmail())) {
                    throw new IllegalArgumentException("Email '" + updatedStaff.getEmail() + "' already exists");
                }
                existingStaff.setEmail(updatedStaff.getEmail());
            }
            if (updatedStaff.getPhone() != null) {
                existingStaff.setPhone(updatedStaff.getPhone());
            }
            if (updatedStaff.getRole() != null) {
                existingStaff.setRole(updatedStaff.getRole());
            }
            if (updatedStaff.getDepartment() != null) {
                existingStaff.setDepartment(updatedStaff.getDepartment());
            }
            if (updatedStaff.getSalary() != null) {
                existingStaff.setSalary(updatedStaff.getSalary());
            }
            if (updatedStaff.getIsActive() != null) {
                existingStaff.setIsActive(updatedStaff.getIsActive());
            }

            return staffRepository.save(existingStaff);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to update staff: " + e.getMessage(), e);
        }
    }

    /**
     * Deactivate staff member
     * @param id the staff ID
     * @return the updated staff
     */
    public Staff deactivateStaff(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }

            Optional<Staff> staffOpt = staffRepository.findById(id);
            if (staffOpt.isEmpty()) {
                throw new IllegalArgumentException("Staff with ID " + id + " not found");
            }

            Staff staff = staffOpt.get();
            staff.setIsActive(false);
            return staffRepository.save(staff);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to deactivate staff: " + e.getMessage(), e);
        }
    }

    /**
     * Activate staff member
     * @param id the staff ID
     * @return the updated staff
     */
    public Staff activateStaff(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }

            Optional<Staff> staffOpt = staffRepository.findById(id);
            if (staffOpt.isEmpty()) {
                throw new IllegalArgumentException("Staff with ID " + id + " not found");
            }

            Staff staff = staffOpt.get();
            staff.setIsActive(true);
            return staffRepository.save(staff);

        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation errors
        } catch (Exception e) {
            throw new RuntimeException("Failed to activate staff: " + e.getMessage(), e);
        }
    }

    /**
     * Count staff by role
     * @param role the staff role
     * @return number of staff with the specified role
     */
    public long countStaffByRole(StaffRole role) {
        try {
            if (role == null) {
                throw new IllegalArgumentException("Role cannot be null");
            }
            return staffRepository.countByRole(role);
        } catch (Exception e) {
            throw new RuntimeException("Failed to count staff by role: " + e.getMessage(), e);
        }
    }

    /**
     * Count active staff
     * @return number of active staff members
     */
    public long countActiveStaff() {
        try {
            return staffRepository.countByIsActive(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to count active staff: " + e.getMessage(), e);
        }
    }

    /**
     * Search staff by name
     * @param searchTerm the search term
     * @return list of staff matching the search term
     */
    public List<Staff> searchStaffByName(String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return getAllStaff();
            }
            return staffRepository.findByNameContaining(searchTerm, searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Failed to search staff by name: " + e.getMessage(), e);
        }
    }

    /**
     * Assign a table to a staff member
     * @param staffId the staff ID
     * @param tableId the table ID
     * @return the updated staff member
     * @throws IllegalArgumentException if staff or table not found
     */
    public Staff assignTableToStaff(Long staffId, Long tableId) {
        try {
            if (staffId == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }
            if (tableId == null) {
                throw new IllegalArgumentException("Table ID cannot be null");
            }

            Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff with ID " + staffId + " not found"));

            RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Table with ID " + tableId + " not found"));

            if (staff.getAssignedTables() == null) {
                staff.setAssignedTables(new java.util.ArrayList<>());
            }

            if (!staff.getAssignedTables().contains(table)) {
                staff.getAssignedTables().add(table);
                return staffRepository.save(staff);
            }

            return staff;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign table to staff: " + e.getMessage(), e);
        }
    }

    /**
     * Unassign a table from a staff member
     * @param staffId the staff ID
     * @param tableId the table ID
     * @return the updated staff member
     * @throws IllegalArgumentException if staff or table not found
     */
    public Staff unassignTableFromStaff(Long staffId, Long tableId) {
        try {
            if (staffId == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }
            if (tableId == null) {
                throw new IllegalArgumentException("Table ID cannot be null");
            }

            Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff with ID " + staffId + " not found"));

            if (staff.getAssignedTables() != null) {
                staff.getAssignedTables().removeIf(table -> table.getId().equals(tableId));
                return staffRepository.save(staff);
            }

            return staff;

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to unassign table from staff: " + e.getMessage(), e);
        }
    }

    /**
     * Get all tables assigned to a staff member
     * @param staffId the staff ID
     * @return list of assigned tables
     */
    public List<RestaurantTable> getAssignedTables(Long staffId) {
        try {
            if (staffId == null) {
                throw new IllegalArgumentException("Staff ID cannot be null");
            }

            Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff with ID " + staffId + " not found"));

            return staff.getAssignedTables() != null ? staff.getAssignedTables() : new java.util.ArrayList<>();

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get assigned tables: " + e.getMessage(), e);
        }
    }
}
