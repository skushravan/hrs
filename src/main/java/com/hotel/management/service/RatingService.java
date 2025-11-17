package com.hotel.management.service;

import com.hotel.management.entity.Rating;
import com.hotel.management.enums.RatingStatus;
import com.hotel.management.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    public Rating submitRating(Rating rating) {
        if (rating.getDate() == null) {
            rating.setDate(LocalDateTime.now());
        }
        rating.setStatus(RatingStatus.PENDING);
        return ratingRepository.save(rating);
    }

    public Rating approveRating(Long ratingId) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isPresent()) {
            Rating rating = ratingOpt.get();
            rating.setStatus(RatingStatus.APPROVED);
            return ratingRepository.save(rating);
        }
        throw new IllegalArgumentException("Rating not found with ID: " + ratingId);
    }

    public Rating rejectRating(Long ratingId) {
        Optional<Rating> ratingOpt = ratingRepository.findById(ratingId);
        if (ratingOpt.isPresent()) {
            Rating rating = ratingOpt.get();
            rating.setStatus(RatingStatus.REJECTED);
            return ratingRepository.save(rating);
        }
        throw new IllegalArgumentException("Rating not found with ID: " + ratingId);
    }

    public Double getAverageRating() {
        try {
            Double average = ratingRepository.findAverageRatingByStatus(RatingStatus.APPROVED);
            return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public List<Rating> getAllRatings() {
        try {
            List<Rating> ratings = ratingRepository.findAllByOrderByDateDesc();
            return ratings != null ? ratings : new java.util.ArrayList<>();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    public List<Rating> getRatingsByStatus(RatingStatus status) {
        try {
            List<Rating> ratings = ratingRepository.findByStatusOrderByDateDesc(status);
            return ratings != null ? ratings : new java.util.ArrayList<>();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    public List<Rating> getApprovedRatings() {
        try {
            List<Rating> ratings = ratingRepository.findByStatusOrderByDateDesc(RatingStatus.APPROVED);
            return ratings != null ? ratings : new java.util.ArrayList<>();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    public List<Rating> getPendingRatings() {
        try {
            List<Rating> ratings = ratingRepository.findByStatusOrderByDateDesc(RatingStatus.PENDING);
            return ratings != null ? ratings : new java.util.ArrayList<>();
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    public Optional<Rating> getRatingById(Long id) {
        return ratingRepository.findById(id);
    }

    public Long getPendingRatingsCount() {
        try {
            Long count = ratingRepository.countByStatus(RatingStatus.PENDING);
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    public Long getApprovedRatingsCount() {
        try {
            Long count = ratingRepository.countByStatus(RatingStatus.APPROVED);
            return count != null ? count : 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    public String getRatingDistribution() {
        StringBuilder distribution = new StringBuilder();
        for (int i = 5; i >= 1; i--) {
            Long count = ratingRepository.countByRatingAndStatus(i, RatingStatus.APPROVED);
            distribution.append(i).append(" stars: ").append(count).append(" | ");
        }
        return distribution.toString();
    }
}