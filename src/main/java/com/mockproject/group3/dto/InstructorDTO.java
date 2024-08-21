package com.mockproject.group3.dto;

import java.util.Set;

import com.mockproject.group3.model.Payout;
import com.mockproject.group3.model.Subscription;
import com.mockproject.group3.model.Users;

public class InstructorDTO {
    private String instructor_code;
    private String instructorName;
    private String profession_experience;
    private double fee;
    private Set<Payout> payout;
    private Set<Subscription> subscription;
    private Users user;

    public String getInstructor_code() {
        return instructor_code;
    }

    public void setInstructor_code(String instructor_code) {
        this.instructor_code = instructor_code;
    }

    public String getProfession_experience() {
        return profession_experience;
    }

    public void setProfession_experience(String profession_experience) {
        this.profession_experience = profession_experience;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public Set<Payout> getPayout() {
        return payout;
    }

    public void setPayout(Set<Payout> payout) {
        this.payout = payout;
    }

    public Set<Subscription> getSubscription() {
        return subscription;
    }

    public void setSubscription(Set<Subscription> subscription) {
        this.subscription = subscription;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
    
}
