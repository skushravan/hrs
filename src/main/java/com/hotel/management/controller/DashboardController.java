package com.hotel.management.controller;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.Staff;
import com.hotel.management.enums.TableStatus;
import com.hotel.management.repository.UserRepository;
import com.hotel.management.service.ReservationService;
import com.hotel.management.service.StaffService;
import com.hotel.management.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for handling dashboard requests for different user roles
 */
@Controller
public class DashboardController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TableService tableService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Admin Dashboard
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, Authentication authentication) {
        try {
            LocalDate today = LocalDate.now();
            
            // Get statistics for admin
            List<Reservation> todayReservations = reservationService.getReservationsForDate(today);
            model.addAttribute("todayReservationsCount", todayReservations.size());
            
            long availableTablesCount = tableService.countTablesByStatus(TableStatus.AVAILABLE);
            model.addAttribute("availableTablesCount", availableTablesCount);
            
            List<Staff> activeStaff = staffService.getActiveStaff();
            model.addAttribute("activeStaffCount", activeStaff.size());
            
            long totalTables = tableService.countTablesByStatus(TableStatus.AVAILABLE) + 
                             tableService.countTablesByStatus(TableStatus.OCCUPIED) + 
                             tableService.countTablesByStatus(TableStatus.RESERVED);
            model.addAttribute("totalTables", totalTables);
            
            // Total users count
            long totalUsers = userRepository.count();
            model.addAttribute("totalUsers", totalUsers);
            
            return "admin-dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load admin dashboard: " + e.getMessage());
            return "admin-dashboard";
        }
    }

    /**
     * Staff Dashboard
     */
    @GetMapping("/staff/dashboard")
    public String staffDashboard(Model model, Authentication authentication) {
        try {
            LocalDate today = LocalDate.now();
            
            // Get statistics for staff
            List<Reservation> todayReservations = reservationService.getReservationsForDate(today);
            model.addAttribute("todayReservationsCount", todayReservations.size());
            
            long availableTablesCount = tableService.countTablesByStatus(TableStatus.AVAILABLE);
            model.addAttribute("availableTablesCount", availableTablesCount);
            
            long reservedTablesCount = tableService.countTablesByStatus(TableStatus.RESERVED);
            model.addAttribute("reservedTablesCount", reservedTablesCount);
            
            long occupiedTablesCount = tableService.countTablesByStatus(TableStatus.OCCUPIED);
            model.addAttribute("occupiedTablesCount", occupiedTablesCount);
            
            // Recent reservations
            List<Reservation> recentReservations = todayReservations.stream()
                    .limit(5)
                    .toList();
            model.addAttribute("recentReservations", recentReservations);
            
            // Task counts (placeholder - implement when TaskService is available)
            model.addAttribute("pendingTasksCount", 0);
            model.addAttribute("completedTasksCount", 0);
            
            return "staff-dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load staff dashboard: " + e.getMessage());
            return "staff-dashboard";
        }
    }

    /**
     * Customer Dashboard
     */
    @GetMapping("/customer/dashboard")
    public String customerDashboard(Model model, Authentication authentication) {
        try {
            // Get customer-specific data
            // Note: This assumes reservations are linked to users. Adjust based on your data model.
            List<Reservation> allReservations = reservationService.getAllReservations();
            model.addAttribute("myReservationsCount", allReservations.size());
            model.addAttribute("myReservations", allReservations.stream().limit(5).collect(Collectors.toList()));
            
            // Upcoming reservations (future dates)
            LocalDate today = LocalDate.now();
            long upcomingCount = allReservations.stream()
                    .filter(r -> r.getReservationTime().toLocalDate().isAfter(today) || 
                                r.getReservationTime().toLocalDate().equals(today))
                    .count();
            model.addAttribute("upcomingReservationsCount", upcomingCount);
            
            // Ratings count (placeholder)
            model.addAttribute("myRatingsCount", 0);
            
            return "customer-dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load customer dashboard: " + e.getMessage());
            return "customer-dashboard";
        }
    }
}

