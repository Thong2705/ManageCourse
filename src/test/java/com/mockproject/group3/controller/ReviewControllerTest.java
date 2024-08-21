package com.mockproject.group3.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mockproject.group3.dto.ReviewDTO;
import com.mockproject.group3.model.Review;
import com.mockproject.group3.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private Review review;
    private ReviewDTO reviewDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();

        reviewDTO = new ReviewDTO();
        reviewDTO.setRating(5);
        reviewDTO.setComment("Great course!");

        review = new Review();
        review.setId(1);
        review.setRating(5);
        review.setComment("Great course!");
    }

    @Test
    void testGetAllReviews() throws Exception {
        List<Review> reviews = Arrays.asList(review);
        when(reviewService.getAllReviews()).thenReturn(reviews);

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(review.getId()))
                .andExpect(jsonPath("$[0].rating").value(review.getRating()))
                .andExpect(jsonPath("$[0].comment").value(review.getComment()));
    }

    @Test
    void testCreateReview() throws Exception {
        when(reviewService.createReview(any(ReviewDTO.class), anyInt())).thenReturn(review);

        mockMvc.perform(post("/api/reviews/course/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()))
                .andExpect(jsonPath("$.rating").value(review.getRating()))
                .andExpect(jsonPath("$.comment").value(review.getComment()));
    }

    @Test
    void testUpdateReview() throws Exception {
        when(reviewService.updateReview(any(ReviewDTO.class), anyInt())).thenReturn(review);

        mockMvc.perform(put("/api/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()))
                .andExpect(jsonPath("$.rating").value(review.getRating()))
                .andExpect(jsonPath("$.comment").value(review.getComment()));
    }

}
