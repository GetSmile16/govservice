package org.example.service;

import jakarta.validation.Valid;
import org.example.dto.product.DoneProductDto;
import org.example.dto.product.UserProductDto;
import org.example.dto.user.UserIdDto;
import org.example.dto.user.UserInfoDto;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface UserService {
    UserIdDto createUser(@Valid UserInfoDto userInfo);

    UserProductDto provideSeasonProduct(String username, Long productId);

    UserProductDto provideProduct(String username, Long productId);

    List<DoneProductDto> getAllProductsByUserOrderByDateDesc(String username);

    UserProductDto getProductByUser(String username, Long id);
}
