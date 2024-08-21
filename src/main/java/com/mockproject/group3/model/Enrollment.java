package com.mockproject.group3.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mockproject.group3.enums.EnrollmentStatus;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Set;

@Entity
@Table(name = "enrollment", uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "course_id"})})
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    @CreationTimestamp
    private LocalDateTime enrollmentDate;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @JsonBackReference
    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL)
    private Set<EnrollmentLessonDetail> enrollmentLessonDetails;

    public Enrollment() {

    }

    public Enrollment(int id, String description, EnrollmentStatus status, Student student, Course course, Set<EnrollmentLessonDetail> enrollmentLessonDetails) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.student = student;
        this.course = course;
        this.enrollmentLessonDetails = enrollmentLessonDetails;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }


    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<EnrollmentLessonDetail> getEnrollmentLessonDetails() {
        return enrollmentLessonDetails;
    }

    public void setEnrollmentLessonDetails(Set<EnrollmentLessonDetail> enrollmentLessonDetails) {
        this.enrollmentLessonDetails = enrollmentLessonDetails;
    }


}
