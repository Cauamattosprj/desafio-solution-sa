package com.cauamattosprj.solutionsa.controllers;

import com.cauamattosprj.solutionsa.dtos.viacep.ViaCepResponse;
import com.cauamattosprj.solutionsa.services.CepService;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/viacep")
public class ViaCepController {

    private final CepService cepService;

    public ViaCepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<ViaCepResponse> getCep(
            @PathVariable
            @Pattern(regexp = "\\d{5}-\\d{3}", message = "CEP deve estar no formato 00000-000")
            String cep) {
        ViaCepResponse response = cepService.lookup(cep);
        return ResponseEntity.ok(response);
    }
}
