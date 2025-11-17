package com.hotel.management.controller;

import com.hotel.management.entity.Rating;
import com.hotel.management.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);

    @Autowired
    private RatingService ratingService;

    @GetMapping
    public String getAllRatings(Model model) {
        try {
            List<Rating> ratings = ratingService.getAllRatings();
            Double averageRating = ratingService.getAverageRating();
            Long pendingCount = ratingService.getPendingRatingsCount();
            
            model.addAttribute("ratings", ratings != null ? ratings : new ArrayList<>());
            model.addAttribute("averageRating", averageRating != null ? averageRating : 0.0);
            model.addAttribute("pendingCount", pendingCount != null ? pendingCount : 0L);
            model.addAttribute("totalRatings", ratings != null ? ratings.size() : 0);
            
            return "ratings";
        } catch (Exception e) {
            logger.error("Error loading ratings: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load ratings: " + e.getMessage());
            model.addAttribute("ratings", new ArrayList<>());
            model.addAttribute("averageRating", 0.0);
            model.addAttribute("pendingCount", 0L);
            model.addAttribute("totalRatings", 0);
            return "ratings";
        }
    }

    @GetMapping("/new")
    public String showRatingForm(Model model) {
        model.addAttribute("rating", new Rating());
        return "rating-form";
    }

    @PostMapping
    public String submitRating(@ModelAttribute Rating rating, RedirectAttributes redirectAttributes) {
        try {
            ratingService.submitRating(rating);
            redirectAttributes.addFlashAttribute("success", "Thank you for your rating! It will be reviewed soon.");
            return "redirect:/ratings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to submit rating: " + e.getMessage());
            return "redirect:/ratings/new";
        }
    }

    @PostMapping("/{id}/approve")
    public String approveRating(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ratingService.approveRating(id);
            redirectAttributes.addFlashAttribute("success", "Rating approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve rating: " + e.getMessage());
        }
        return "redirect:/ratings";
    }

    @PostMapping("/{id}/reject")
    public String rejectRating(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ratingService.rejectRating(id);
            redirectAttributes.addFlashAttribute("success", "Rating rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject rating: " + e.getMessage());
        }
        return "redirect:/ratings";
    }

    @GetMapping("/pending")
    public String getPendingRatings(Model model) {
        try {
            List<Rating> pendingRatings = ratingService.getPendingRatings();
            model.addAttribute("ratings", pendingRatings != null ? pendingRatings : new ArrayList<>());
            model.addAttribute("averageRating", ratingService.getAverageRating());
            model.addAttribute("pendingCount", pendingRatings != null ? pendingRatings.size() : 0);
            model.addAttribute("totalRatings", pendingRatings != null ? pendingRatings.size() : 0);
            model.addAttribute("filter", "pending");
            return "ratings";
        } catch (Exception e) {
            logger.error("Error loading pending ratings: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load pending ratings: " + e.getMessage());
            model.addAttribute("ratings", new ArrayList<>());
            model.addAttribute("averageRating", 0.0);
            model.addAttribute("pendingCount", 0L);
            model.addAttribute("totalRatings", 0);
            return "ratings";
        }
    }

    @GetMapping("/approved")
    public String getApprovedRatings(Model model) {
        try {
            List<Rating> approvedRatings = ratingService.getApprovedRatings();
            model.addAttribute("ratings", approvedRatings != null ? approvedRatings : new ArrayList<>());
            model.addAttribute("averageRating", ratingService.getAverageRating());
            model.addAttribute("pendingCount", ratingService.getPendingRatingsCount());
            model.addAttribute("totalRatings", approvedRatings != null ? approvedRatings.size() : 0);
            model.addAttribute("filter", "approved");
            return "ratings";
        } catch (Exception e) {
            logger.error("Error loading approved ratings: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load approved ratings: " + e.getMessage());
            model.addAttribute("ratings", new ArrayList<>());
            model.addAttribute("averageRating", 0.0);
            model.addAttribute("pendingCount", 0L);
            model.addAttribute("totalRatings", 0);
            return "ratings";
        }
    }
}