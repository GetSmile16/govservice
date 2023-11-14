package org.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_retry")
public class EmailRetry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String addressTo;
    private String subject;
    private String content;
    private LocalDateTime retryTime;

    public EmailRetry() {
    }

    public EmailRetry(Long id, String addressTo, String subject, String content, LocalDateTime retryTime) {
        this.id = id;
        this.addressTo = addressTo;
        this.subject = subject;
        this.content = content;
        this.retryTime = retryTime;
    }

    public EmailRetry(String addressTo, String subject, String content) {
        this.addressTo = addressTo;
        this.subject = subject;
        this.content = content;
    }

    @PrePersist
    void init() {
        retryTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddressTo() {
        return addressTo;
    }

    public void setAddressTo(String addressTo) {
        this.addressTo = addressTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(LocalDateTime retryTime) {
        this.retryTime = retryTime;
    }
}

