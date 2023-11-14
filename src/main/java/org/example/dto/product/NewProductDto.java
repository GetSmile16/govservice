package org.example.dto.product;

import jakarta.validation.constraints.NotBlank;

public class NewProductDto {
    @NotBlank
    private String productName;
    private Integer remainingCount;

    public NewProductDto() {
    }

    public NewProductDto(String productName) {
        this.productName = productName;
    }

    public NewProductDto(String productName, Integer remainingCount) {
        this.productName = productName;
        this.remainingCount = remainingCount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }
}
