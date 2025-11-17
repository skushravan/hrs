package com.hotel.management.repository;

import com.hotel.management.entity.Rating;
import com.hotel.management.enums.RatingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    List<Rating> findByStatus(RatingStatus status);
    
    List<Rating> findByRating(Integer rating);
    
    List<Rating> findByStatusOrderByDateDesc(RatingStatus status);
    
    List<Rating> findAllByOrderByDateDesc();
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.status = :status")
    Double findAverageRatingByStatus(@Param("status") RatingStatus status);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.status = :status")
    Long countByStatus(@Param("status") RatingStatus status);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.rating = :rating AND r.status = :status")
    Long countByRatingAndStatus(@Param("rating") Integer rating, @Param("status") RatingStatus status);
}