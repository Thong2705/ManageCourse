package com.mockproject.group3.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseApiPaginationRespone<T> extends BaseApiResponse<T> {
    private int currentPage;
    private int pageSize;
    private int totalPage;
    private int totalItem;

    public BaseApiPaginationRespone() {

    }

    public BaseApiPaginationRespone(int currentPage, int pageSize, int totalPage, int totalItem) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalItem = totalItem;
    }

    public BaseApiPaginationRespone(int code, String message, T payload, int currentPage, int pageSize, int totalPage,
            int totalItem) {
        super(code, message, payload);
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalItem = totalItem;
    }

    public BaseApiPaginationRespone(int code, String message, int currentPage, int pageSize, int totalPage,
            int totalItem) {
        super(code, message);
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalItem = totalItem;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

}
