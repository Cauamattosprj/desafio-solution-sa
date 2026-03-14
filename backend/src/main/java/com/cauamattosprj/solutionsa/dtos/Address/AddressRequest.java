package com.cauamattosprj.solutionsa.dtos.address;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
public class AddressRequest {

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 00000-000")
    private String cep;

    @Size(max = 20)
    private String number;

    @Size(max = 100)
    private String complement;

    @Size(max = 200)
    private String street;

    @Size(max = 100)
    private String neighborhood;

    @Size(max = 100)
    private String city;

    @Pattern(regexp = "^[A-Za-z]{2}$", message = "Estado deve ser a sigla com 2 letras")
    private String state;

    private boolean main = false;
}