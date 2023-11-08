package org.example.service;

import org.example.dto.UserIdDto;
import org.example.dto.UserInfoDto;
import org.example.model.User;

public interface UserService {
    User getUserByEmail(String email);

    UserIdDto createUser(UserInfoDto userInfo);
}
