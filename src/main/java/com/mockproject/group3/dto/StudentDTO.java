package com.mockproject.group3.dto;

import com.mockproject.group3.model.Users;

public class StudentDTO {
    private String student_code;
    private Users user;

    public String getStudent_code() {
        return student_code;
    }

    public void setStudent_code(String student_code) {
        this.student_code = student_code;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
