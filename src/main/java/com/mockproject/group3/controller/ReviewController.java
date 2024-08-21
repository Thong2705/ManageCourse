package com.mockproject.group3.controller;

import com.mockproject.group3.dto.ReviewDTO;
import com.mockproject.group3.model.Review;
import com.mockproject.group3.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // Create CRUD methods for Review
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/course/{courseId}")
    public ResponseEntity<Review> createReview(@RequestBody @Valid ReviewDTO reviewDTO, @PathVariable Integer courseId) {
        Review review = reviewService.createReview(reviewDTO, courseId);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Integer id, @RequestBody @Valid ReviewDTO reviewDTO) {
        Review review = reviewService.updateReview(reviewDTO, id);
        return ResponseEntity.ok(review);
    }

}
