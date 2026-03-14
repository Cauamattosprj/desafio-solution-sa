package com.cauamattosprj.solutionsa.dtos.user;

import com.cauamattosprj.solutionsa.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserResponse {

    private UUID id;
    private String cpf;
    private String name;
    private User.Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.id        = user.getId();
        response.cpf       = user.getCpf();
        response.name      = user.getName();
        response.role      = user.getRole();
        response.createdAt = user.getCreatedAt();
        response.updatedAt = user.getUpdatedAt();
        return response;
    }
}