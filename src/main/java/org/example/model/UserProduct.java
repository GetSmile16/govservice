package org.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users_products")
public class UserProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    private LocalDateTime dateOfCreated;

    @PrePersist
    void init() {
        dateOfCreated = LocalDateTime.now();
    }

    public UserProduct() {
    }

    public UserProduct(Long id, User user, Product product) {
        this.id = id;
        this.user = user;
        this.product = product;
    }

    public UserProduct(Long id, Product product, LocalDateTime localDateTime) {
        this.id = id;
        this.product = product;
        this.dateOfCreated = localDateTime;
    }

    public UserProduct(User user, Product product) {
        this.user = user;
        this.product = product;
    }

    public UserProduct(Long id, Product product) {
        this.id = id;
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public LocalDateTime getDateOfCreated() {
        return dateOfCreated;
    }
}
