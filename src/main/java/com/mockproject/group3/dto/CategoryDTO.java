package com.mockproject.group3.dto;

import com.mockproject.group3.model.Category;

import java.time.LocalDateTime;

public class CategoryDTO {

    private String name;
    private String description;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    public CategoryDTO() {
    }

    public CategoryDTO(String name, String description, Category parentCategory) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }

    public void setUpdatedAt(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
