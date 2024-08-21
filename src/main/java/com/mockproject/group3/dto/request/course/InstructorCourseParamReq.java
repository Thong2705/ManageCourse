package com.mockproject.group3.dto.request.course;

import com.mockproject.group3.dto.request.PaginationParamReq;

public class InstructorCourseParamReq extends PaginationParamReq {
    private String keyword = "";

    public InstructorCourseParamReq() {
    }

    public InstructorCourseParamReq(String keyword) {
        this.keyword = keyword;
    }

    public InstructorCourseParamReq(int page, int pageSize, String keyword) {
        super(page, pageSize);
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

}
