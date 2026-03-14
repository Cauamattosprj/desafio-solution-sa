package com.cauamattosprj.solutionsa.dtos.user;

import com.cauamattosprj.solutionsa.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;

    @NotNull(message = "Perfil é obrigatório")
    private User.Role role;
}