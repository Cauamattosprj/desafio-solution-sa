package com.cauamattosprj.solutionsa;

import com.cauamattosprj.solutionsa.dtos.auth.RegisterRequest;
import com.cauamattosprj.solutionsa.models.User;
import com.cauamattosprj.solutionsa.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        registerUser("111.444.777-35", "Admin User", "password123", User.Role.ADMIN);
        registerUser("529.982.247-25", "Common User", "password123", User.Role.USER);
    }

    private void registerUser(String cpf, String name, String password, User.Role role) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setCpf(cpf);
        request.setName(name);
        request.setPassword(password);
        request.setRole(role);

        mockMvc.perform(post("/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    @Test
    void adminShouldListAllUsers() throws Exception {
        mockMvc.perform(get("/users")
                .with(httpBasic("111.444.777-35", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void commonUserShouldNotListAllUsers() throws Exception {
        mockMvc.perform(get("/users")
                .with(httpBasic("529.982.247-25", "password123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedUserShouldNotAccessEndpoints() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void commonUserShouldViewOwnData() throws Exception {
        String id = userRepository.findByCpf("529.982.247-25")
                .orElseThrow().getId().toString();

        mockMvc.perform(get("/users/" + id)
                .with(httpBasic("529.982.247-25", "password123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("529.982.247-25"));
    }

    @Test
    void commonUserShouldNotViewAnotherUserData() throws Exception {
        String adminId = userRepository.findByCpf("111.444.777-35")
                .orElseThrow().getId().toString();

        mockMvc.perform(get("/users/" + adminId)
                .with(httpBasic("529.982.247-25", "password123")))
                .andExpect(status().isForbidden());
    }
}
