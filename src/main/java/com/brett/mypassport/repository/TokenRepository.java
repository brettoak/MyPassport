package com.brett.mypassport.repository;

import com.brett.mypassport.entity.Token;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and (t.expired = false and t.revoked = false)\s
            """)
    List<Token> findAllValidTokensByUser(Long id);

    @Query(value = """
            select t from Token t inner join User u\s
            on t.user.id = u.id\s
            where u.id = :id and (t.expired = false and t.revoked = false)\s
            """)
    Page<Token> findAllValidTokensByUser(Long id, Pageable pageable);

    Optional<Token> findByToken(String token);

    Optional<Token> findByRefreshToken(String refreshToken);
}
