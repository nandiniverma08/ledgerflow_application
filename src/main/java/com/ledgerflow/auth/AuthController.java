package com.ledgerflow.auth;

import com.ledgerflow.auth.dto.LoginRequest;
import com.ledgerflow.auth.dto.RegisterRequest;
import com.ledgerflow.common.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        ApiResponse<Void> response = ApiResponse.success(
                "User registered successfully",
                null
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody LoginRequest request) {

        authService.login(request);

        ApiResponse<Void> response = ApiResponse.success(
                "Login successful",
                null
        );

        return ResponseEntity.ok(response);

    }
}
