package com.mockproject.group3.dto.response.course;

import com.mockproject.group3.model.Course;

public class CourseWithRatingRes extends Course {
    // private Course course;
    private double avarageRating = 0.0;

    public CourseWithRatingRes() {
    }

    public CourseWithRatingRes(Course course, double avarageRating) {
        super(course);
        this.avarageRating = avarageRating;
    }

    public double getAvarageRating() {
        return avarageRating;
    }

    public void setAvarageRating(double avarageRating) {
        this.avarageRating = avarageRating;
    }

}
