package org.example.dto.product;

import java.time.LocalDateTime;

public class UserProductDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String productName;
    private LocalDateTime dateOfCreated;
    private boolean season;

    public UserProductDto() {
    }

    public UserProductDto(Long id, String email, String firstName, String lastName, String patronymic, String productName, LocalDateTime dateOfCreated, boolean season) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.productName = productName;
        this.dateOfCreated = dateOfCreated;
        this.season = season;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public LocalDateTime getDateOfCreated() {
        return dateOfCreated;
    }

    public void setDateOfCreated(LocalDateTime dateOfCreated) {
        this.dateOfCreated = dateOfCreated;
    }

    public boolean getSeason() {
        return season;
    }

    public void setSeason(boolean season) {
        this.season = season;
    }
}
