package org.example.service;

import org.example.exception.UserNotFound;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final MessageSource messageSource;


    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("Username: " + email);

        User user = userRepository.findByEmail(email);

        if (user == null) {
            String message = messageSource.getMessage("user.not_found", null, Locale.getDefault());
            log.warn(message);
            throw new UserNotFound(message);
        }

        return user;
    }
}
