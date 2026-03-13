package com.cauamattosprj.solutionsa;

import com.cauamattosprj.solutionsa.dto.auth.LoginRequest;
import com.cauamattosprj.solutionsa.dto.auth.RegisterRequest;
import com.cauamattosprj.solutionsa.models.User;
import com.cauamattosprj.solutionsa.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setCpf("123.456.789-09");
        request.setName("Caua Mattos");
        request.setPassword("password123");
        request.setRole(User.Role.USER);

        mockMvc.perform(post("/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"))
                .andExpect(jsonPath("$.name").value("Caua Mattos"));
    }

    @Test
    void shouldRejectDuplicateCpfRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setCpf("123.456.789-09");
        request.setName("Caua Mattos");
        request.setPassword("password123");
        request.setRole(User.Role.USER);

        mockMvc.perform(post("/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(post("/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setCpf("123.456.789-09");
        register.setName("Caua Mattos");
        register.setPassword("password123");
        register.setRole(User.Role.USER);

        mockMvc.perform(post("/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest();
        login.setCpf("123.456.789-09");
        login.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value("123.456.789-09"));
    }

    @Test
    void shouldRejectLoginWithWrongPassword() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setCpf("123.456.789-09");
        register.setName("Caua Mattos");
        register.setPassword("password123");
        register.setRole(User.Role.USER);

        mockMvc.perform(post("/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest();
        login.setCpf("123.456.789-09");
        login.setPassword("wrongPassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
