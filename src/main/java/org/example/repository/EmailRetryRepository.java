package org.example.repository;

import org.example.model.EmailRetry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailRetryRepository extends JpaRepository<EmailRetry, Long> {
    List<EmailRetry> findAllByOrderByRetryTimeAsc();
}
