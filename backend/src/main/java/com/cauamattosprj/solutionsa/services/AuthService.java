package com.cauamattosprj.solutionsa.services;

import com.cauamattosprj.solutionsa.dtos.auth.LoginRequest;
import com.cauamattosprj.solutionsa.dtos.auth.RegisterRequest;
import com.cauamattosprj.solutionsa.dtos.user.UserResponse;
import com.cauamattosprj.solutionsa.models.User;
import com.cauamattosprj.solutionsa.repositories.UserRepository;
import com.cauamattosprj.solutionsa.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }

        User user = User.builder()
                .cpf(request.getCpf())
                .name(request.getName())
                .password_hash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getCpf(), request.getPassword())
            );

            User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();
            return UserResponse.from(user);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas", ex);
        }
    }
}