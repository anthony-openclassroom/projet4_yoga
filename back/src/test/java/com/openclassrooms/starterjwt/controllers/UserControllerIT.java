package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User savedUser;
    private String authToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        savedUser = userRepository.save(
                new User("user@test.com", "Doe", "John", passwordEncoder.encode("password"), false));
        authToken = generateToken(savedUser);
    }

    @Test
    void findById_returnsUser_whenFound() throws Exception {
        mockMvc.perform(get("/api/user/" + savedUser.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void findById_returnsNotFound_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/user/9999")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_returnsBadRequest_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/user/abc")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_returnsOk_whenOwner() throws Exception {
        mockMvc.perform(delete("/api/user/" + savedUser.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returnsUnauthorized_whenNotOwner() throws Exception {
        User otherUser = userRepository.save(
                new User("other@test.com", "Other", "User", passwordEncoder.encode("pass"), false));
        String otherToken = generateToken(otherUser);

        mockMvc.perform(delete("/api/user/" + savedUser.getId())
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_returnsNotFound_whenUserDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/user/9999")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    private String generateToken(User user) {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(user.getId())
                .username(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .admin(user.isAdmin())
                .password(user.getPassword())
                .build();
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        return jwtUtils.generateJwtToken(auth);
    }
}
