package com.sang.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.sang.demo.enums.RoleName;
import com.sang.demo.models.User;
import com.sang.demo.repositories.UserRepository;
import com.sang.demo.security.JwtService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User personnelUser;

    @BeforeEach
    void setUp() {
        if (userRepository.findByEmail("personnel@test.com").isEmpty()) {
            personnelUser = userRepository.save(User.builder()
                    .nom("Test")
                    .prenom("Personnel")
                    .email("personnel@test.com")
                    .motDePasse(passwordEncoder.encode("password123"))
                    .role(RoleName.PERSONNEL)
                    .build());
        } else {
            personnelUser = userRepository.findByEmail("personnel@test.com").get();
        }
    }

    @Test
    void accessProtectedEndpoint_WithoutToken_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/poches"))
                .andExpect(status().isForbidden());
    }

    @Test
    void accessPublicEndpoint_WithoutToken_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/hopitaux"))
                .andExpect(status().isOk());
    }

    @Test
    void accessAdminEndpoint_WithPersonnelRole_ShouldReturn403() throws Exception {
        String token = jwtService.generateToken(personnelUser);

        mockMvc.perform(get("/api/v1/admin/utilisateurs")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
