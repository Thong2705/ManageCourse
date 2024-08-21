package com.mockproject.group3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mockproject.group3.model.Payout;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Integer> {

}
