package org.example.dto.product;

import java.time.LocalDateTime;

public class DoneProductDto {
    private Long id;
    private LocalDateTime dateOfCreated;
    private String productName;
    private boolean season;

    public DoneProductDto() {
    }

    public DoneProductDto(Long id, String productName, boolean season) {
        this.id = id;
        this.productName = productName;
        this.season = season;
    }

    public DoneProductDto(Long id, String productName, LocalDateTime dateOfCreated, boolean season) {
        this.id = id;
        this.dateOfCreated = dateOfCreated;
        this.productName = productName;
        this.season = season;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateOfCreated() {
        return dateOfCreated;
    }

    public void setDateOfCreated(LocalDateTime dateOfCreated) {
        this.dateOfCreated = dateOfCreated;
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
