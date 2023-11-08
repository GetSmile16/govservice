package org.example.service;

import jakarta.transaction.Transactional;
import org.example.dto.UserIdDto;
import org.example.dto.UserInfoDto;
import org.example.exception.UserIsExist;
import org.example.model.User;
import org.example.model.enums.Role;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private PasswordEncoder encoder;
    private MessageSource messageSource;

    public UserServiceImpl() {
    }

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.encoder = passwordEncoder;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserIdDto createUser(UserInfoDto userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail());
        if (user != null) {
            String message = messageSource.getMessage("user.is_exist", null, Locale.getDefault());
            log.warn(message);
            throw new UserIsExist(message);
        }

        user = new User();
        user.setEmail(userInfo.getEmail());
        user.setPassword(encoder.encode(userInfo.getPassword()));
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());

        Set<Role> roles = user.getRoles();
        roles.add(Role.USER);

        user.setRoles(roles);
        userRepository.save(user);
        log.info("User with username \"" + user.getUsername() + "\" created");

        return new UserIdDto(userRepository.save(user).getId());
    }
}
