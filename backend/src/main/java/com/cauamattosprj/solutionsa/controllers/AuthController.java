package com.cauamattosprj.solutionsa.controllers;

import com.cauamattosprj.solutionsa.dtos.auth.LoginRequest;
import com.cauamattosprj.solutionsa.dtos.auth.RegisterRequest;
import com.cauamattosprj.solutionsa.dtos.user.UserResponse;
import com.cauamattosprj.solutionsa.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/user")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        UserResponse response = authService.register(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}