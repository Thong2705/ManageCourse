package com.mockproject.group3.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "instructor")
public class Instructor {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "instructor_code", unique = true)
    private String instructor_code;
    @Column(name = "fee")
    private double fee;
    @Column(name = "profession_experience")
    private String profession_experience;

    @OneToMany(mappedBy = "instructor")
    private Set<Payout> payouts;

    @OneToMany(mappedBy = "instructor")
    private Set<Subscription> subscriptions;

    @OneToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @JsonManagedReference
    @OneToMany(mappedBy = "instructor")
    private Set<Course> courses;

    public Instructor(int id, String instructor_code, double fee, String profession_experience, Set<Payout> payouts,
            Set<Subscription> subscriptions) {
        this.id = id;
        this.instructor_code = instructor_code;
        this.fee = fee;
        this.profession_experience = profession_experience;
        this.payouts = payouts;
        this.subscriptions = subscriptions;
    }

    public Instructor() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInstructor_code() {
        return instructor_code;
    }

    public void setInstructor_code(String instructor_code) {
        this.instructor_code = instructor_code;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getProfession_experience() {
        return profession_experience;
    }

    public void setProfession_experience(String profession_experience) {
        this.profession_experience = profession_experience;
    }

    public Set<Payout> getPayouts() {
        return payouts;
    }

    public void setPayouts(Set<Payout> payouts) {
        this.payouts = payouts;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }
}
