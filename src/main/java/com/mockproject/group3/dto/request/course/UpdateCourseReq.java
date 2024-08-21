package com.mockproject.group3.dto.request.course;

import java.util.List;

import com.mockproject.group3.dto.request.lesson.UpdateLessonReq;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateCourseReq {
    @NotNull(message = "FIELD_REQUIRED")
    private String title;

    @NotNull(message = "FIELD_REQUIRED")
    private String description;

    @Min(value = 0, message = "INVALID_MINVALUE")
    private double price;

    @NotNull
    private int categoryId;

    private List<UpdateLessonReq> lessons;

    public UpdateCourseReq() {
    }

    public UpdateCourseReq(String title, String description, double price, int categoryId,
            List<UpdateLessonReq> lessons) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.lessons = lessons;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public List<UpdateLessonReq> getLessons() {
        return lessons;
    }

    public void setLessons(List<UpdateLessonReq> lessons) {
        this.lessons = lessons;
    }

}
