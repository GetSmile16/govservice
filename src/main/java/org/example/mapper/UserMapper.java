package org.example.mapper;

import org.example.dto.user.UserInfoDto;
import org.example.model.User;
import org.example.model.enums.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Set;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "roles", target = "roles")
    User userInfoToUser(UserInfoDto userInfo, Set<Role> roles);

    @Mapping(source = "id", target = "id")
    User userInfoToUserWithId(UserInfoDto userInfo, Long id);
}
