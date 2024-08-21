package com.mockproject.group3.dto.request.subscription;

public class SubscriptionReq {
    private int idInstructor;

    public SubscriptionReq() {
    }

    public SubscriptionReq(int idInstructor) {
        this.idInstructor = idInstructor;
    }

    public int getIdInstructor() {
        return idInstructor;
    }

    public void setIdInstructor(int idInstructor) {
        this.idInstructor = idInstructor;
    }
}
