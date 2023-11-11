package org.example.service;

import jakarta.validation.Valid;
import org.example.dto.UserIdDto;
import org.example.dto.UserInfoDto;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserService {
    UserIdDto createUser(@Valid UserInfoDto userInfo);
}
