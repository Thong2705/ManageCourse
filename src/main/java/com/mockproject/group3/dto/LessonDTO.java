package com.mockproject.group3.dto;

public class LessonDTO {
    private String title;

    private String content;

    private int courseId;

    public String getTitle() {
        return title;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
