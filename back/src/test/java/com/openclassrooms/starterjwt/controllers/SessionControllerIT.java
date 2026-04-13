package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User savedUser;
    private Teacher savedTeacher;
    private Session savedSession;
    private String authToken;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();

        savedUser = userRepository.save(
                new User("user@test.com", "Doe", "John", passwordEncoder.encode("password"), true));
        authToken = generateToken(savedUser);

        savedTeacher = teacherRepository.save(Teacher.builder()
                .lastName("Smith")
                .firstName("Jane")
                .build());

        savedSession = sessionRepository.save(Session.builder()
                .name("Morning Yoga")
                .date(new Date())
                .description("A relaxing morning session")
                .teacher(savedTeacher)
                .users(new ArrayList<>())
                .build());
    }

    // ─── findAll ────────────────────────────────────────────────────────────

    @Test
    void findAll_returnsListOfSessions() throws Exception {
        mockMvc.perform(get("/api/session")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Morning Yoga"));
    }

    // ─── findById ───────────────────────────────────────────────────────────

    @Test
    void findById_returnsSession_whenFound() throws Exception {
        mockMvc.perform(get("/api/session/" + savedSession.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning Yoga"));
    }

    @Test
    void findById_returnsNotFound_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/session/9999")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findById_returnsBadRequest_whenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/session/abc")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    // ─── create ─────────────────────────────────────────────────────────────

    @Test
    void create_returnsCreatedSession() throws Exception {
        SessionDto dto = new SessionDto();
        dto.setName("Evening Yoga");
        dto.setDate(new Date());
        dto.setTeacher_id(savedTeacher.getId());
        dto.setDescription("A calm evening session");

        mockMvc.perform(post("/api/session")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Evening Yoga"));
    }

    // ─── update ─────────────────────────────────────────────────────────────

    @Test
    void update_returnsUpdatedSession() throws Exception {
        SessionDto dto = new SessionDto();
        dto.setName("Updated Yoga");
        dto.setDate(new Date());
        dto.setTeacher_id(savedTeacher.getId());
        dto.setDescription("Updated description");

        mockMvc.perform(put("/api/session/" + savedSession.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Yoga"));
    }

    // ─── delete ─────────────────────────────────────────────────────────────

    @Test
    void delete_returnsOk_whenSessionExists() throws Exception {
        mockMvc.perform(delete("/api/session/" + savedSession.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returnsNotFound_whenSessionDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/session/9999")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    // ─── participate ────────────────────────────────────────────────────────

    @Test
    void participate_returnsOk_whenUserCanJoin() throws Exception {
        User participant = userRepository.save(
                new User("participant@test.com", "Part", "Ici", passwordEncoder.encode("pass"), false));

        mockMvc.perform(post("/api/session/" + savedSession.getId() + "/participate/" + participant.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    void participate_returnsBadRequest_whenAlreadyParticipating() throws Exception {
        savedSession.getUsers().add(savedUser);
        sessionRepository.save(savedSession);

        mockMvc.perform(post("/api/session/" + savedSession.getId() + "/participate/" + savedUser.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    // ─── noLongerParticipate ────────────────────────────────────────────────

    @Test
    void noLongerParticipate_returnsOk_whenUserIsParticipant() throws Exception {
        savedSession.getUsers().add(savedUser);
        sessionRepository.save(savedSession);

        mockMvc.perform(delete("/api/session/" + savedSession.getId() + "/participate/" + savedUser.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    void noLongerParticipate_returnsBadRequest_whenUserIsNotParticipant() throws Exception {
        mockMvc.perform(delete("/api/session/" + savedSession.getId() + "/participate/" + savedUser.getId())
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
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
