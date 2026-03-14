package com.cauamattosprj.solutionsa.dtos.viacep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ViaCepResponse {

    private String cep;

    @JsonProperty("logradouro")
    private String street;

    @JsonProperty("bairro")
    private String neighborhood;

    @JsonProperty("localidade")
    private String city;

    @JsonProperty("uf")
    private String state;

    private Boolean erro;
}
