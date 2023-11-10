package org.example.service;

import org.example.dto.UserIdDto;
import org.example.dto.UserInfoDto;
import org.example.exception.UserIsExist;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MessageSource messageSource;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void createUser_userIsNotExist_savesUser() {
        UserInfoDto userInfo = new UserInfoDto("test@example.com", "Test", "Test", "password");

        when(userRepository.findByEmail(userInfo.getEmail())).thenReturn(null);

        User savedUser = new User();
        savedUser.setEmail(userInfo.getEmail());
        savedUser.setPassword(userInfo.getPassword());
        savedUser.setFirstName(userInfo.getFirstName());
        savedUser.setLastName(userInfo.getLastName());
        savedUser.setId(1L);

        when(passwordEncoder.encode(userInfo.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserIdDto actual = userService.createUser(userInfo);

        assertNotNull(actual);
        assertEquals(1L, actual.getId());

        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();
        assertEquals(userInfo.getEmail(), capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertEquals(userInfo.getFirstName(), capturedUser.getFirstName());
        assertEquals(userInfo.getLastName(), capturedUser.getLastName());
    }

    @Test
    void createUser_userIsExist_throwsException() {
        UserInfoDto userInfo = new UserInfoDto("existingUser@example.com", "password", "John", "Doe");

        when(userRepository.findByEmail(userInfo.getEmail())).thenReturn(new User());

        assertThrows(UserIsExist.class, () -> userService.createUser(userInfo));

        verify(userRepository, never()).save(new User());
    }
}