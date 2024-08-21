package com.mockproject.group3.dto.request.course;

import com.mockproject.group3.enums.Status;

import jakarta.validation.constraints.NotNull;

public class ReviewCourseReq {
    @NotNull(message = "FIELD_REQUIRED")
    private int id;
    private Status status;

    public ReviewCourseReq() {
    }

    public ReviewCourseReq(int id, Status status) {
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}