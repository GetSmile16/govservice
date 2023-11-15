package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.example.dto.jwt.AuthRequestDto;
import org.example.dto.jwt.AuthResponseDto;
import org.example.dto.product.DoneProductDto;
import org.example.dto.product.UserProductDto;
import org.example.dto.user.UserIdDto;
import org.example.dto.user.UserInfoDto;
import org.example.model.User;
import org.example.service.JwtService;
import org.example.service.UserService;
import org.example.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserService service;
    private final JwtService jwtService;
    private final EmailUtil emailUtil;

    @Autowired
    public UserController(UserService service,
                          JwtService jwtService,
                          EmailUtil emailUtil) {
        this.service = service;
        this.jwtService = jwtService;
        this.emailUtil = emailUtil;
    }

    @Operation(summary = "Create user")
    @PostMapping("auth/register")
    public ResponseEntity<UserIdDto> addNewUser(@RequestBody UserInfoDto userInfo) {
        return ResponseEntity.ok(service.createUser(userInfo));
    }

    @Operation(summary = "Generate token for user with creds in body")
    @PostMapping("auth/generateToken")
    public ResponseEntity<AuthResponseDto> authenticateAndGetToken(@RequestBody AuthRequestDto authRequest) {
        return ResponseEntity.ok(jwtService.generateToken(authRequest));
    }

    @Operation(summary = "Provide seasonal service for current user")
    @PostMapping("user/services/season/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserProductDto> provideSeasonProduct(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(service.provideSeasonProduct(principal.getName(), id));
    }

    @Operation(summary = "Provide common service for current user")
    @PostMapping("user/services/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserProductDto> provideProduct(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(service.provideProduct(principal.getName(), id));
    }

    @Operation(summary = "Get completed services. Field \"season\" indicates that the service is seasonal")
    @GetMapping("user/services/done")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<DoneProductDto>> getDoneProducts(Principal principal) {
        return ResponseEntity.ok(service.getAllProductsByUserOrderByDateDesc(principal.getName()));
    }

    @GetMapping("user/services/done/{id}")
    @Operation(summary = "Get completed service by ID. Field \"season\" indicates that the service is seasonal")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<UserProductDto> getDoneProduct(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(service.getProductByUser(principal.getName(), id));
    }

    @GetMapping("/test/email")
    public ResponseEntity<Void> getDoneProduct() {
        emailUtil.sendEmailFailed(
                new User(
                        "dlawotakk@gmail.com",
                        "First",
                        "Last",
                        "Patronyc"
                ),
                "Test name");
        return ResponseEntity.ok().build();
    }
}
