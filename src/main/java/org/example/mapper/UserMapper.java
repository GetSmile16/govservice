package org.example.mapper;

import org.example.dto.UserIdDto;
import org.example.dto.UserInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserIdDto userInfoToUserId(UserInfoDto userInfo);
}
