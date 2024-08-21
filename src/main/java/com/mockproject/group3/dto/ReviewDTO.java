package com.mockproject.group3.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ReviewDTO {
    @Min(value = 0, message = "INVALID_MINVALUE")
    @Max(value = 5, message = "INVALID_MAXVALUE")
    private int rating;
    private String comment;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
