package org.example.dto.product;

public class ProductIdDto {
    private Long id;

    public ProductIdDto() {
    }

    public ProductIdDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
