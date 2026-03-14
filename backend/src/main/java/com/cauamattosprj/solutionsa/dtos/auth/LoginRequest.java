package com.cauamattosprj.solutionsa.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}