package com.cauamattosprj.solutionsa;

import com.cauamattosprj.solutionsa.dto.auth.RegisterRequest;
import com.cauamattosprj.solutionsa.models.User;
import com.cauamattosprj.solutionsa.repository.AddressRepository;
import com.cauamattosprj.solutionsa.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ViaCepIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired AddressRepository addressRepository;

    @BeforeEach
    void setUp() throws Exception {
        addressRepository.deleteAll();
        userRepository.deleteAll();

        RegisterRequest request = new RegisterRequest();
        request.setCpf("529.982.247-25");
        request.setName("Common User");
        request.setPassword("password123");
        request.setRole(User.Role.USER);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .post("/auth/user")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Test
    void shouldReturnAddressDataForValidCep() throws Exception {
        mockMvc.perform(get("/viacep/01001-000")
                .with(httpBasic("529.982.247-25", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").value("01001-000"))
                .andExpect(jsonPath("$.street").isNotEmpty())
                .andExpect(jsonPath("$.city").isNotEmpty())
                .andExpect(jsonPath("$.state").isNotEmpty());
    }

    @Test
    void shouldReturn404ForNonExistentCep() throws Exception {
        mockMvc.perform(get("/viacep/00000-000")
                .with(httpBasic("529.982.247-25", "password123")))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400ForMalformedCep() throws Exception {
        mockMvc.perform(get("/viacep/invalid")
                .with(httpBasic("529.982.247-25", "password123")))
                .andExpect(status().isBadRequest());
    }
}
