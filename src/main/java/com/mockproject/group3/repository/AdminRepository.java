package com.mockproject.group3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockproject.group3.model.Users;

@Repository
public interface AdminRepository extends JpaRepository<Users, Integer> {

}
