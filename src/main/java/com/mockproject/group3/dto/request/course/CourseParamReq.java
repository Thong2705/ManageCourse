package com.mockproject.group3.dto.request.course;

import com.mockproject.group3.dto.request.PaginationParamReq;
import com.mockproject.group3.validators.ParamPattern;

public class CourseParamReq extends PaginationParamReq {

    private String keyword = "";
    private int categoryId;

    @ParamPattern(regexp = "title", message = "INVALID_PARAM")
    private String[] sortBy = {};
    @ParamPattern(regexp = "asc|desc|ASC|DESC", message = "INVALID_PARAM")
    private String[] sortDirection = {};

    public CourseParamReq() {
    }

    public CourseParamReq(String keyword, int categoryId, String[] sortBy, String[] sortDirection) {
        this.keyword = keyword;
        this.categoryId = categoryId;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    public CourseParamReq(int page, int pageSize, String keyword, int categoryId, String[] sortBy,
            String[] sortDirection) {
        super(page, pageSize);
        this.keyword = keyword;
        this.categoryId = categoryId;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String[] getSortBy() {
        return sortBy;
    }

    public void setSortBy(String[] sortBy) {
        this.sortBy = sortBy;
    }

    public String[] getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String[] sortDirection) {
        this.sortDirection = sortDirection;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
