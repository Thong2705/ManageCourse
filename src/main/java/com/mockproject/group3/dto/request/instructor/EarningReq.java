package com.mockproject.group3.dto.request.instructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class EarningReq {
    @NotNull(message = "FIELD_REQUIRED")
    @Pattern(regexp = "\\b(1[0-2]|[1-9])\\b", message = "INVALID_PARAM")
    private String month;

    @NotNull(message = "FIELD_REQUIRED")
    @Pattern(regexp = "\\b\\d{4}\\b", message = "INVALID_PARAM")
    private String year;

    public EarningReq() {
    }

    public EarningReq(@Pattern(regexp = "\\b(1[0-2]|[1-9])\\b", message = "INVALID_PARAM") String month,
            @Pattern(regexp = "\\b\\d{4}\\b", message = "INVALID_PARAM") String year) {
        this.month = month;
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

}
