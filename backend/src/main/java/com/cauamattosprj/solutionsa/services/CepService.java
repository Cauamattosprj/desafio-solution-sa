package com.cauamattosprj.solutionsa.services;

import com.cauamattosprj.solutionsa.dtos.viacep.ViaCepResponse;

public interface CepService {

    ViaCepResponse lookup(String cep);
}
