package com.hotel.management.controller;


import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.Staff;
import com.hotel.management.enums.TableStatus;

import com.hotel.management.service.RatingService;
import com.hotel.management.service.ReservationService;
import com.hotel.management.service.StaffService;
import com.hotel.management.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TableService tableService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private RatingService ratingService;

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication authentication) {
        // Redirect to role-specific dashboard if user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            
            // Check for ADMIN role (highest priority)
            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();
                if (role.equals("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                }
            }
            
            // Check for STAFF role (includes ROLE_STAFF, ROLE_MANAGER, ROLE_RECEPTIONIST)
            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();
                if (role.equals("ROLE_STAFF") || role.equals("ROLE_MANAGER") || role.equals("ROLE_RECEPTIONIST")) {
                    return "redirect:/staff/dashboard";
                }
            }
            
            // Check for CUSTOMER role (ROLE_USER)
            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();
                if (role.equals("ROLE_USER")) {
                    return "redirect:/customer/dashboard";
                }
            }
        }
        
        // If not authenticated or no matching role, show default dashboard
        try {
            // Get dashboard statistics
            LocalDate today = LocalDate.now();

            // Today's reservations
            List<Reservation> todayReservations = reservationService.getReservationsForDate(today);
            model.addAttribute("todayReservationsCount", todayReservations.size());

            // Available tables
            long availableTablesCount = tableService.countTablesByStatus(TableStatus.AVAILABLE);
            model.addAttribute("availableTablesCount", availableTablesCount);

            // Active staff members
            List<Staff> activeStaff = staffService.getActiveStaff();
            model.addAttribute("activeStaffCount", activeStaff.size());

          // Replace inventory related code with:
model.addAttribute("averageRating", ratingService.getAverageRating());
model.addAttribute("pendingReviewsCount", ratingService.getPendingRatingsCount());
model.addAttribute("recentRatings", ratingService.getApprovedRatings().stream().limit(5).collect(Collectors.toList()));

            // Recent reservations (last 5)
            List<Reservation> recentReservations = todayReservations.stream()
                    .limit(5)
                    .toList();
            model.addAttribute("recentReservations", recentReservations);

            // Table status summary
            model.addAttribute("totalTables", tableService.countTablesByStatus(TableStatus.AVAILABLE) + 
                    tableService.countTablesByStatus(TableStatus.OCCUPIED) + 
                    tableService.countTablesByStatus(TableStatus.RESERVED));
            model.addAttribute("occupiedTablesCount", tableService.countTablesByStatus(TableStatus.OCCUPIED));
            model.addAttribute("reservedTablesCount", tableService.countTablesByStatus(TableStatus.RESERVED));

            return "index";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load dashboard: " + e.getMessage());
            return "index";
        }
    }

    @GetMapping("/index")
    public String index(Model model, Authentication authentication) {
        return home(model, authentication);
    }
}


