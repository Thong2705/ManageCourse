package com.mockproject.group3.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    private String content;

    @CreationTimestamp
    private LocalDateTime created_at;

    @UpdateTimestamp
    private LocalDateTime updated_at;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

    @JsonBackReference
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<EnrollmentLessonDetail> enrollmentLessonDetails;

    private int old_id;

    public Lesson() {

    }

    public Lesson(int id, String title, String content,
            Course course, Set<EnrollmentLessonDetail> enrollmentLessonDetails, int old_id) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.course = course;
        this.enrollmentLessonDetails = enrollmentLessonDetails;
        this.old_id = old_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
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

    public int getOld_id() {
        return old_id;
    }

    public void setOld_id(int old_id) {
        this.old_id = old_id;
    }
}
