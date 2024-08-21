package com.mockproject.group3.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "Student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "student_code", unique = true)
    private String student_code;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private Users user;

    @JsonManagedReference
    @OneToMany(mappedBy = "student")
    private Set<Subscription> subscriptions;

    @JsonBackReference
    @OneToMany(mappedBy = "student")
    private Set<Report> reports;

    @JsonBackReference
    @OneToMany(mappedBy = "student")
    private Set<Review> reviews;

    @JsonManagedReference
    @OneToMany(mappedBy = "student")
    private Set<Payment> payments;

    @JsonManagedReference
    @OneToMany(mappedBy = "student")
    private Set<Enrollment> enrollments;

    
    public Student(int id, String student_code, Users user, Set<Subscription> subscriptions, Set<Report> reports,
            Set<Review> reviews, Set<Payment> payments, Set<Enrollment> enrollments) {
        this.id = id;
        this.student_code = student_code;
        this.user = user;
        this.subscriptions = subscriptions;
        this.reports = reports;
        this.reviews = reviews;
        this.payments = payments;
        this.enrollments = enrollments;
    }

    public Student() {

    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudent_code() {
        return this.student_code;
    }

    public void setStudent_code(String student_code) {
        this.student_code = student_code;
    }

    public Users getUser() {
        return this.user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Set<Report> getReports() {
        return reports;
    }

    public void setReports(Set<Report> reports) {
        this.reports = reports;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public Set<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Set<Payment> payments) {
        this.payments = payments;
    }

    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    

}
