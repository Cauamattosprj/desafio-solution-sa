package com.cauamattosprj.solutionsa;

import com.cauamattosprj.solutionsa.dto.address.AddressRequest;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AddressIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired AddressRepository addressRepository;

    private UUID userId;

    @BeforeEach
    void setUp() throws Exception {
        addressRepository.deleteAll();
        userRepository.deleteAll();

        RegisterRequest request = new RegisterRequest();
        request.setCpf("529.982.247-25");
        request.setName("Common User");
        request.setPassword("password123");
        request.setRole(User.Role.USER);

        mockMvc.perform(post("/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        userId = userRepository.findByCpf("529.982.247-25").orElseThrow().getId();
    }

    private AddressRequest buildAddress(boolean main) {
        AddressRequest req = new AddressRequest();
        req.setCep("21530-100");
        req.setNumber("100");
        req.setStreet("Rua Mauricio");
        req.setNeighborhood("Coelho Neto");
        req.setCity("Rio de Janeiro");
        req.setState("RJ");
        req.setMain(main);
        return req;
    }

    @Test
    void shouldCreateAddressSuccessfully() throws Exception {
        mockMvc.perform(post("/users/" + userId + "/addresses")
                .with(httpBasic("529.982.247-25", "senha123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildAddress(true))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.street").value("Rua Mauricio"))
                .andExpect(jsonPath("$.main").value(true));
    }

    @Test
    void shouldListAddressesByUser() throws Exception {
        mockMvc.perform(post("/users/" + userId + "/addresses")
                .with(httpBasic("529.982.247-25", "senha123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildAddress(true))));

        mockMvc.perform(get("/users/" + userId + "/addresses")
                .with(httpBasic("529.982.247-25", "senha123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldUnsetPreviousMainWhenNewMainIsDefined() throws Exception {
        String firstId = objectMapper.readTree(
                mockMvc.perform(post("/users/" + userId + "/addresses")
                        .with(httpBasic("529.982.247-25", "password123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildAddress(true))))
                        .andReturn().getResponse().getContentAsString()
        ).get("id").asText();

        mockMvc.perform(post("/users/" + userId + "/addresses")
                .with(httpBasic("529.982.247-25", "senha123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildAddress(true))));

        mockMvc.perform(get("/users/" + userId + "/addresses/" + firstId)
                .with(httpBasic("529.982.247-25", "senha123")))
                .andExpect(jsonPath("$.main").value(false));
    }

    @Test
    void shouldPromoteAnotherAddressWhenMainIsDeleted() throws Exception {
        mockMvc.perform(post("/users/" + userId + "/addresses")
                .with(httpBasic("529.982.247-25", "senha123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildAddress(true))));

        mockMvc.perform(post("/users/" + userId + "/addresses")
                .with(httpBasic("529.982.247-25", "senha123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buildAddress(false))));

        String mainId = objectMapper.readTree(
                mockMvc.perform(get("/users/" + userId + "/addresses")
                        .with(httpBasic("529.982.247-25", "senha123")))
                        .andReturn().getResponse().getContentAsString()
        ).findValuesAsText("id").stream()
                .filter(id -> {
                    try {
                        String body = mockMvc.perform(get("/users/" + userId + "/addresses/" + id)
                                .with(httpBasic("529.982.247-25", "senha123")))
                                .andReturn().getResponse().getContentAsString();
                        return objectMapper.readTree(body).get("main").asBoolean();
                    } catch (Exception e) { return false; }
                }).findFirst().orElseThrow();

        mockMvc.perform(delete("/users/" + userId + "/addresses/" + mainId)
                .with(httpBasic("529.982.247-25", "senha123")))
                .andExpect(status().isNoContent());

        String listBody = mockMvc.perform(get("/users/" + userId + "/addresses")
                .with(httpBasic("529.982.247-25", "senha123")))
                .andReturn().getResponse().getContentAsString();

        long mainCount = objectMapper.readTree(listBody).findValues("main")
                .stream().filter(n -> n.asBoolean()).count();

        assert mainCount == 1;
    }

    @Test
    void shouldNotAllowAccessToAnotherUsersAddresses() throws Exception {
        RegisterRequest otherRequest = new RegisterRequest();
        otherRequest.setCpf("111.444.777-35");
        otherRequest.setName("Other User");
        otherRequest.setPassword("password123");
        otherRequest.setRole(User.Role.USER);

        mockMvc.perform(post("/auth/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otherRequest)));

        UUID otherUserId = userRepository.findByCpf("111.444.777-35").orElseThrow().getId();

        mockMvc.perform(get("/users/" + otherUserId + "/addresses")
                .with(httpBasic("529.982.247-25", "senha123")))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateAddressSuccessfully() throws Exception {
        String addressId = objectMapper.readTree(
                mockMvc.perform(post("/users/" + userId + "/addresses")
                        .with(httpBasic("529.982.247-25", "senha123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildAddress(true))))
                        .andReturn().getResponse().getContentAsString()
        ).get("id").asText();

        AddressRequest update = buildAddress(true);
        update.setNumber("999");

        mockMvc.perform(put("/users/" + userId + "/addresses/" + addressId)
                .with(httpBasic("529.982.247-25", "senha123"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("999"));
    }
}
