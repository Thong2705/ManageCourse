package com.mockproject.group3.dto.request.course;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.enums.Status;

public class AdminCourseParamReq extends PaginationParamReq {
    private String title = "";
    // need validate
    private Status status;

    public AdminCourseParamReq() {
    }

    public AdminCourseParamReq(String title, Status status) {
        this.title = title;
        this.status = status;
    }

    public AdminCourseParamReq(int page, int pageSize, String title, Status status) {
        super(page, pageSize);
        this.title = title;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
