package com.cauamattosprj.solutionsa.services;

import com.cauamattosprj.solutionsa.dtos.user.UserResponse;
import com.cauamattosprj.solutionsa.dtos.user.UserUpdateRequest;
import com.cauamattosprj.solutionsa.models.User;
import com.cauamattosprj.solutionsa.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> findAll() {
        User currentUser = getAuthenticatedUser();
        if (currentUser.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        User currentUser = getAuthenticatedUser();
        if (currentUser.getRole() != User.Role.ADMIN && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Acesso negado");
        }

        return UserResponse.from(user);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof com.cauamattosprj.solutionsa.security.UserDetailsImpl userDetails)) {
            throw new AccessDeniedException("Acesso negado");
        }

        return userDetails.getUser();
    }

    @Transactional
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        User currentUser = getAuthenticatedUser();
        if (currentUser.getRole() != User.Role.ADMIN && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Acesso negado");
        }

        user.setName(request.getName());
        user.setRole(request.getRole());

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        User currentUser = getAuthenticatedUser();
        if (currentUser.getRole() != User.Role.ADMIN && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("Acesso negado");
        }

        userRepository.deleteById(id);
    }
}