package com.hotel.management.controller;

import com.hotel.management.entity.RestaurantTable;
import com.hotel.management.entity.Staff;
import com.hotel.management.enums.StaffRole;
import com.hotel.management.service.StaffService;
import com.hotel.management.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling staff-related web requests
 */
@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private TableService tableService;

    /**
     * Display all staff members
     * @param model the model to pass data to the view
     * @return the staff list view
     */
    @GetMapping
    public String getAllStaff(Model model) {
        try {
            List<Staff> staff = staffService.getAllStaff();
            List<RestaurantTable> allTables = tableService.getAllTables();
            
            model.addAttribute("staff", staff);
            model.addAttribute("allTables", allTables);
            model.addAttribute("staffRoles", StaffRole.values());
            
            // Add a new Staff object for the form
            Staff newStaff = new Staff();
            newStaff.setHireDate(LocalDate.now());
            model.addAttribute("newStaff", newStaff);
            
            // Add counts for each role
            model.addAttribute("totalCount", staff.size());
            model.addAttribute("activeCount", staffService.countActiveStaff());
            
            return "staff";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load staff: " + e.getMessage());
            return "staff";
        }
    }

    /**
     * Display the new staff form
     * @param model the model to pass data to the view
     * @return the new staff form view
     */
    @GetMapping("/new")
    public String showNewStaffForm(Model model) {
        try {
            Staff staff = new Staff();
            staff.setHireDate(LocalDate.now()); // Default to today
            
            model.addAttribute("staff", staff);
            model.addAttribute("staffRoles", StaffRole.values());
            model.addAttribute("departments", List.of("Front Office", "Housekeeping", "Kitchen", "Maintenance", "Security", "Management"));
            
            return "staff-form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load staff form: " + e.getMessage());
            return "staff-form";
        }
    }

    /**
     * Handle form submission to create a new staff member
     * @param staff the staff data from the form
     * @param redirectAttributes attributes for redirect
     * @return redirect to staff list
     */
    @PostMapping
    public String createStaff(@ModelAttribute Staff staff, RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (staff.getFirstName() == null || staff.getFirstName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "First name is required");
                return "redirect:/staff/new";
            }

            if (staff.getLastName() == null || staff.getLastName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Last name is required");
                return "redirect:/staff/new";
            }

            if (staff.getEmail() == null || staff.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email is required");
                return "redirect:/staff/new";
            }

            if (staff.getRole() == null) {
                redirectAttributes.addFlashAttribute("error", "Role is required");
                return "redirect:/staff/new";
            }

            if (staff.getHireDate() == null) {
                redirectAttributes.addFlashAttribute("error", "Hire date is required");
                return "redirect:/staff/new";
            }

            // Create the staff member
            Staff savedStaff = staffService.createStaff(staff);
            
            redirectAttributes.addFlashAttribute("success", 
                "Staff member " + savedStaff.getFullName() + " created successfully");
            
            return "redirect:/staff";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create staff member: " + e.getMessage());
            return "redirect:/staff/new";
        }
    }

    /**
     * Display staff by role
     * @param role the role to filter by
     * @param model the model to pass data to the view
     * @return the staff list view
     */
    @GetMapping("/role/{role}")
    public String getStaffByRole(@PathVariable StaffRole role, Model model) {
        try {
            List<Staff> staff = staffService.getStaffByRole(role);
            model.addAttribute("staff", staff);
            model.addAttribute("selectedRole", role);
            model.addAttribute("staffRoles", StaffRole.values());
            model.addAttribute("totalCount", staff.size());
            model.addAttribute("activeCount", staffService.countActiveStaff());
            
            return "staff";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load staff by role: " + e.getMessage());
            return "redirect:/staff";
        }
    }

    /**
     * Display staff by department
     * @param department the department to filter by
     * @param model the model to pass data to the view
     * @return the staff list view
     */
    @GetMapping("/department/{department}")
    public String getStaffByDepartment(@PathVariable String department, Model model) {
        try {
            List<Staff> staff = staffService.getStaffByDepartment(department);
            model.addAttribute("staff", staff);
            model.addAttribute("selectedDepartment", department);
            model.addAttribute("staffRoles", StaffRole.values());
            model.addAttribute("totalCount", staff.size());
            model.addAttribute("activeCount", staffService.countActiveStaff());
            
            return "staff";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load staff by department: " + e.getMessage());
            return "redirect:/staff";
        }
    }

    /**
     * Display staff details
     * @param id the staff ID
     * @param model the model to pass data to the view
     * @return the staff details view
     */
    @GetMapping("/{id}")
    public String getStaffDetails(@PathVariable Long id, Model model) {
        try {
            var staffOpt = staffService.getStaffById(id);
            if (staffOpt.isEmpty()) {
                model.addAttribute("error", "Staff member not found");
                return "redirect:/staff";
            }
            
            model.addAttribute("staff", staffOpt.get());
            model.addAttribute("staffRoles", StaffRole.values());
            
            return "staff-details";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load staff details: " + e.getMessage());
            return "redirect:/staff";
        }
    }

    /**
     * Display the edit staff form
     * @param id the staff ID
     * @param model the model to pass data to the view
     * @return the edit staff form view
     */
    @GetMapping("/{id}/edit")
    public String showEditStaffForm(@PathVariable Long id, Model model) {
        try {
            var staffOpt = staffService.getStaffById(id);
            if (staffOpt.isEmpty()) {
                model.addAttribute("error", "Staff member not found");
                return "redirect:/staff";
            }
            
            model.addAttribute("staff", staffOpt.get());
            model.addAttribute("staffRoles", StaffRole.values());
            model.addAttribute("departments", List.of("Front Office", "Housekeeping", "Kitchen", "Maintenance", "Security", "Management"));
            
            return "staff-edit";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load staff for editing: " + e.getMessage());
            return "redirect:/staff";
        }
    }

    /**
     * Handle form submission to update a staff member
     * @param id the staff ID
     * @param staff the updated staff data
     * @param redirectAttributes attributes for redirect
     * @return redirect to staff list
     */
    @PostMapping("/{id}/edit")
    public String updateStaff(@PathVariable Long id, @ModelAttribute Staff staff, RedirectAttributes redirectAttributes) {
        try {
            Staff updatedStaff = staffService.updateStaff(id, staff);
            
            redirectAttributes.addFlashAttribute("success", 
                "Staff member " + updatedStaff.getFullName() + " updated successfully");
            
            return "redirect:/staff";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update staff member: " + e.getMessage());
            return "redirect:/staff/" + id + "/edit";
        }
    }

    /**
     * Toggle staff active status
     * @param id the staff ID
     * @param redirectAttributes attributes for redirect
     * @return redirect to staff list
     */
    @PostMapping("/{id}/toggle-status")
    public String toggleStaffStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            var staffOpt = staffService.getStaffById(id);
            if (staffOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Staff member not found");
                return "redirect:/staff";
            }

            Staff staff = staffOpt.get();
            if (staff.getIsActive()) {
                staffService.deactivateStaff(id);
                redirectAttributes.addFlashAttribute("success", 
                    "Staff member " + staff.getFullName() + " deactivated successfully");
            } else {
                staffService.activateStaff(id);
                redirectAttributes.addFlashAttribute("success", 
                    "Staff member " + staff.getFullName() + " activated successfully");
            }
            
            return "redirect:/staff";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update staff status: " + e.getMessage());
            return "redirect:/staff";
        }
    }

    /**
     * Search staff by name
     * @param searchTerm the search term
     * @param model the model to pass data to the view
     * @return the staff list view
     */
    @GetMapping("/search")
    public String searchStaff(@RequestParam String searchTerm, Model model) {
        try {
            List<Staff> staff = staffService.searchStaffByName(searchTerm);
            List<RestaurantTable> allTables = tableService.getAllTables();
            
            model.addAttribute("staff", staff);
            model.addAttribute("allTables", allTables);
            model.addAttribute("searchTerm", searchTerm);
            model.addAttribute("staffRoles", StaffRole.values());
            
            // Add a new Staff object for the form
            Staff newStaff = new Staff();
            newStaff.setHireDate(LocalDate.now());
            model.addAttribute("newStaff", newStaff);
            
            model.addAttribute("totalCount", staff.size());
            model.addAttribute("activeCount", staffService.countActiveStaff());
            
            return "staff";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to search staff: " + e.getMessage());
            return "redirect:/staff";
        }
    }

    /**
     * Assign a table to a staff member
     * @param staffId the staff ID
     * @param tableId the table ID
     * @param redirectAttributes attributes for redirect
     * @return redirect to staff list
     */
    @PostMapping("/{staffId}/assign-table")
    public String assignTable(@PathVariable Long staffId, 
                             @RequestParam Long tableId,
                             RedirectAttributes redirectAttributes) {
        try {
            staffService.assignTableToStaff(staffId, tableId);
            redirectAttributes.addFlashAttribute("success", "Table assigned successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to assign table: " + e.getMessage());
        }
        return "redirect:/staff";
    }

    /**
     * Unassign a table from a staff member
     * @param staffId the staff ID
     * @param tableId the table ID
     * @param redirectAttributes attributes for redirect
     * @return redirect to staff list
     */
    @PostMapping("/{staffId}/unassign-table")
    public String unassignTable(@PathVariable Long staffId,
                               @RequestParam Long tableId,
                               RedirectAttributes redirectAttributes) {
        try {
            staffService.unassignTableFromStaff(staffId, tableId);
            redirectAttributes.addFlashAttribute("success", "Table unassigned successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to unassign table: " + e.getMessage());
        }
        return "redirect:/staff";
    }
}
