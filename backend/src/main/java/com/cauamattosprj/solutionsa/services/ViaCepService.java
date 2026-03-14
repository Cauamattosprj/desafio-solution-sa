package com.cauamattosprj.solutionsa.services;

import com.cauamattosprj.solutionsa.dtos.viacep.ViaCepResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.regex.Pattern;

@Service
public class ViaCepService implements CepService {

    private static final Pattern CEP_PATTERN = Pattern.compile("\\d{5}-\\d{3}");
    private final RestTemplate restTemplate;
    private final java.util.concurrent.ConcurrentMap<String, ViaCepResponse> cache = new java.util.concurrent.ConcurrentHashMap<>();

    public ViaCepService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ViaCepResponse lookup(String cep) {
        if (!CEP_PATTERN.matcher(cep).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido");
        }

        return cache.computeIfAbsent(cep, this::fetchFromViaCep);
    }

    private ViaCepResponse fetchFromViaCep(String cep) {
        String url = String.format("https://viacep.com.br/ws/%s/json/", cep);

        try {
            ViaCepResponse response = restTemplate.getForObject(url, ViaCepResponse.class);
            if (response == null || Boolean.TRUE.equals(response.getErro())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado");
            }

            response.setCep(cep);
            return response;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado", ex);
        }
    }
}
