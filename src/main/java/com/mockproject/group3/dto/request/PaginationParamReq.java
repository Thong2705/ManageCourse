package com.mockproject.group3.dto.request;

import jakarta.validation.constraints.Min;

public class PaginationParamReq {
    @Min(value = 1, message = "INVALID_MINVALUE")
    private int page = 1;

    @Min(value = 1, message = "INVALID_MINVALUE")
    private int pageSize = 10;

    public PaginationParamReq() {
    }

    public PaginationParamReq(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
