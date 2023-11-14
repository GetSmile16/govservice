package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "season_products")
public class SeasonProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer remainingCount;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public SeasonProduct() {
    }

    public SeasonProduct(Long id, Integer remainingCount) {
        this.id = id;
        this.remainingCount = remainingCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
