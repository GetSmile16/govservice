package org.example.dto.user;

public class UserIdDto {
    private Long id;

    public UserIdDto() {
    }

    public UserIdDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
