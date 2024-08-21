package com.mockproject.group3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mockproject.group3.model.Token;

public interface TokenRepository extends JpaRepository<Token, Integer>{

    @Query("""
        select t from Token t inner join Users u on t.user.id = u.id
        where t.user.id = :userId and t.loggedOut = false
    """)
    List<Token> findAllAccessTokenByUser(Integer userId);

    Optional<Token> findByAccessToken(String token);

    Optional<Token> findByRefreshToken(String token);

}
