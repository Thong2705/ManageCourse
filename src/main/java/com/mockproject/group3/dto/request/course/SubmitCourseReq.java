package com.mockproject.group3.dto.request.course;

import jakarta.validation.constraints.NotNull;

public class SubmitCourseReq {
    @NotNull(message = "FIELD_REQUIRED")
    private int id;

    public SubmitCourseReq() {
    }

    public SubmitCourseReq(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
