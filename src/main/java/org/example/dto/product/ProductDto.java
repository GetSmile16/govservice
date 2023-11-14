package org.example.dto.product;

import java.time.LocalDateTime;

public class ProductDto {
    private Long id;
    private String productName;
    private boolean season;

    public ProductDto() {
    }

    public ProductDto(Long id, String productName, LocalDateTime dateOfCreated, boolean season) {
        this.id = id;
        this.productName = productName;
        this.season = season;
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

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public boolean isSeason() {
        return season;
    }

    public void setSeason(boolean season) {
        this.season = season;
    }
}

