package com.mockproject.group3.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mockproject.group3.enums.Status;

import jakarta.persistence.*;

@Entity
@Table(name = "Payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "comment")
    private String comment;
    @Column(name = "amount")
    private double amount;
    @Column(name = "payment_date")
    private LocalDateTime payment_date;
    private String txnRef;

    @Enumerated(EnumType.STRING)
    Status status;

    @JsonManagedReference
    @OneToMany(mappedBy = "payment")
    private Set<PaymentDetail> paymentDetails;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    public Payment(int id, String comment, double amount, LocalDateTime payment_date, Status status,
            Set<PaymentDetail> paymentDetails, String txnRef) {
        this.id = id;
        this.comment = comment;
        this.amount = amount;
        this.payment_date = payment_date;
        this.status = status;
        this.paymentDetails = paymentDetails;
        this.txnRef = txnRef;
    }

    public Payment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(LocalDateTime payment_date) {
        this.payment_date = payment_date;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<PaymentDetail> getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(Set<PaymentDetail> paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(String txnRef) {
        this.txnRef = txnRef;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
    
}