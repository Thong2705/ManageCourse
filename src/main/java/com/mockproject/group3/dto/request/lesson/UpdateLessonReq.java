package com.mockproject.group3.dto.request.lesson;

public class UpdateLessonReq extends CreateLessonReq {
    private int id;

    public UpdateLessonReq() {
    }

    // public UpdateLessonReq(int id) {
    //     this.id = id;
    // }

    public UpdateLessonReq(String title, String content, int id) {
        super(title, content);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
