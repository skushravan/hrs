package com.hotel.management.controller;

import com.hotel.management.entity.Reservation;
import com.hotel.management.entity.RestaurantTable;
import com.hotel.management.enums.ReservationStatus;
import com.hotel.management.service.ReservationService;
import com.hotel.management.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for handling reservation-related web requests
 */
@Controller
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TableService tableService;

    /**
     * Display all reservations
     */
 /**
 * Display all reservations
 */
@GetMapping
public String getAllReservations(
        @RequestParam(value = "completedDate", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate completedDate,
        Model model) {
    try {
        List<Reservation> activeReservations = reservationService.getActiveReservations();
        List<Reservation> completedReservations = reservationService.getCompletedReservations(completedDate);

        model.addAttribute("reservations", activeReservations);
        model.addAttribute("completedReservations", completedReservations);
        model.addAttribute("completedDate", completedDate);
        model.addAttribute("completedCount", reservationService.countReservationsByStatus(ReservationStatus.COMPLETED));
        model.addAttribute("currentDate", LocalDate.now());
        return "reservations";
    } catch (Exception e) {
        model.addAttribute("error", "Failed to load reservations: " + e.getMessage());
        return "reservations";
    }
}
    /**
     * Display the new reservation form - FIXED VERSION
     */
    @GetMapping("/new")
    public String showNewReservationForm(Model model) {
        try {
            // Get available tables for the form
            List<RestaurantTable> availableTables = tableService.getAvailableTables();
            
            // Create a new reservation object for the form
            Reservation reservation = new Reservation();
            reservation.setReservationTime(LocalDateTime.now().plusHours(1)); // Default to 1 hour from now
            
            model.addAttribute("reservation", reservation);
            model.addAttribute("availableTables", availableTables);
            model.addAttribute("reservationStatuses", ReservationStatus.values());
            
            return "reservation-form";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load reservation form: " + e.getMessage());
            return "reservation-form";
        }
    }

    /**
     * Handle form submission to create a new reservation
     */
    @PostMapping
    public String createReservation(@ModelAttribute Reservation reservation, 
                                  RedirectAttributes redirectAttributes) {
        try {
            // Validate required fields
            if (reservation.getCustomerName() == null || reservation.getCustomerName().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Customer name is required");
                return "redirect:/reservations/new";
            }

            if (reservation.getCustomerPhone() == null || reservation.getCustomerPhone().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Customer phone is required");
                return "redirect:/reservations/new";
            }

            if (reservation.getTable() == null || reservation.getTable().getId() == null) {
                redirectAttributes.addFlashAttribute("error", "Please select a table");
                return "redirect:/reservations/new";
            }

            if (reservation.getReservationTime() == null) {
                redirectAttributes.addFlashAttribute("error", "Reservation time is required");
                return "redirect:/reservations/new";
            }

            if (reservation.getPartySize() == null || reservation.getPartySize() < 1) {
                redirectAttributes.addFlashAttribute("error", "Party size must be at least 1");
                return "redirect:/reservations/new";
            }

            // Create the reservation
            Reservation savedReservation = reservationService.createReservation(reservation);
            
            redirectAttributes.addFlashAttribute("success", 
                "Reservation created successfully for " + savedReservation.getCustomerName());
            
            return "redirect:/reservations";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservations/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create reservation: " + e.getMessage());
            return "redirect:/reservations/new";
        }
    }

    /**
     * Display reservation details
     */
    @GetMapping("/{id}")
    public String getReservationDetails(@PathVariable Long id, Model model) {
        try {
            Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
            if (reservationOpt.isEmpty()) {
                model.addAttribute("error", "Reservation not found");
                return "redirect:/reservations";
            }
            
            model.addAttribute("reservation", reservationOpt.get());
            return "reservation-details";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load reservation details: " + e.getMessage());
            return "redirect:/reservations";
        }
    }

    /**
     * Update reservation status
     */
    @PostMapping("/{id}/status")
    public String updateReservationStatus(@PathVariable Long id, 
                                        @RequestParam ReservationStatus status,
                                        RedirectAttributes redirectAttributes) {
        try {
            reservationService.updateReservationStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Reservation status updated successfully");
            return "redirect:/reservations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update reservation status: " + e.getMessage());
            return "redirect:/reservations";
        }
    }

    /**
     * Cancel a reservation
     */
    @PostMapping("/{id}/cancel")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("success", "Reservation cancelled successfully");
            return "redirect:/reservations";
        } catch (Exception e) {
            logger.error("Failed to cancel reservation with ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to cancel reservation: " + e.getMessage());
            return "redirect:/reservations";
        }
    }

    /**
     * Display reservations for today
     */
    @GetMapping("/today")
    public String getTodayReservations(Model model) {
        try {
            LocalDate today = LocalDate.now();
            List<Reservation> reservations = reservationService.getReservationsForDate(today);

            List<Reservation> activeToday = reservations.stream()
                    .filter(res -> res.getStatus() != ReservationStatus.COMPLETED)
                    .collect(Collectors.toList());
            List<Reservation> completedToday = reservations.stream()
                    .filter(res -> res.getStatus() == ReservationStatus.COMPLETED)
                    .collect(Collectors.toList());

            model.addAttribute("reservations", activeToday);
            model.addAttribute("completedReservations", completedToday);
            model.addAttribute("completedDate", today);
            model.addAttribute("selectedDate", today);
            model.addAttribute("completedCount", reservationService.countReservationsByStatus(ReservationStatus.COMPLETED));
            model.addAttribute("currentDate", today);
            return "reservations";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load today's reservations: " + e.getMessage());
            return "reservations";
        }
    }
}