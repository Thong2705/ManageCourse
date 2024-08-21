package com.mockproject.group3.dto;

public class SavedCourseDTO {
    private int courseId;
    private String description;

    public SavedCourseDTO(){

    }

    public SavedCourseDTO(int courseId, int studentId, String description) {
        this.courseId = courseId;
        this.description = description;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
