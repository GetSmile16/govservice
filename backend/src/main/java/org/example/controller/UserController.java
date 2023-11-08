package org.example.controller;

import org.example.dto.AuthRequestDto;
import org.example.dto.AuthResponseDto;
import org.example.dto.UserIdDto;
import org.example.dto.UserInfoDto;
import org.example.service.JwtService;
import org.example.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final UserServiceImpl service;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserServiceImpl service, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.service = service;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/admin/profile")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminProfile() {
        return "Welcome to Admin Profile";
    }

    @PostMapping("/user/add")
    public ResponseEntity<UserIdDto> addNewUser(@RequestBody UserInfoDto userInfo) {
        return ResponseEntity.ok(service.createUser(userInfo));
    }

    @PostMapping("/generateToken")
    public ResponseEntity<AuthResponseDto> authenticateAndGetToken(@RequestBody AuthRequestDto authRequest) {
        return ResponseEntity.ok(jwtService.generateToken(authRequest));
    }

}
