package com.mockproject.group3.dto.response.instructor;

public class EarningInstructorRes {
    private int idCourse;
    private String title;
    private long totalPurchase;
    private double totalEarn;

    public EarningInstructorRes() {
    }

    public EarningInstructorRes(int idCourse, String title, long totalPurchase, double totalEarn) {
        this.idCourse = idCourse;
        this.title = title;
        this.totalPurchase = totalPurchase;
        this.totalEarn = totalEarn;
    }

    public int getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(int idCourse) {
        this.idCourse = idCourse;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTotalPurchase() {
        return totalPurchase;
    }

    public void setTotalPurchase(long totalPurchase) {
        this.totalPurchase = totalPurchase;
    }

    public double getTotalEarn() {
        return totalEarn;
    }

    public void setTotalEarn(double totalEarn) {
        this.totalEarn = totalEarn;
    }

}
