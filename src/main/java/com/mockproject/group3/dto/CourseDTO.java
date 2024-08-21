package com.mockproject.group3.dto;

import com.mockproject.group3.enums.Status;

public class CourseDTO {
    private int courseId;
    private String courseName;
    private String courseTitle;
    private String courseDescription;
    private Status courseStatus;
    private double courseRate;
    private String courseCode;

    public CourseDTO() {
    }


    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public Status getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(Status courseStatus) {
        this.courseStatus = courseStatus;
    }

    public double getCourseRate() {
        return courseRate;
    }

    public void setCourseRate(double courseRate) {
        this.courseRate = courseRate;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
