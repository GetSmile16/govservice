package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    @OneToOne(cascade = CascadeType.ALL)
    private SeasonProduct seasonProduct;

    public Product() {

    }

    public Product(Long id, String productName) {
        this.id = id;
        this.productName = productName;
    }

    public Product(String productName) {
        this.productName = productName;
    }

    public Product(Long id, String productName, SeasonProduct seasonProduct) {
        this.id = id;
        this.productName = productName;
        this.seasonProduct = seasonProduct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String name) {
        this.productName = name;
    }

    public SeasonProduct getSeasonProduct() {
        return seasonProduct;
    }

    public void setSeasonProduct(SeasonProduct seasonProduct) {
        this.seasonProduct = seasonProduct;
    }
}
