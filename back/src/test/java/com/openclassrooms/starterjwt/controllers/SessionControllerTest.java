package com.openclassrooms.starterjwt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SessionService sessionService;

    @MockitoBean
    private SessionMapper sessionMapper;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private Session buildSession() {
        return Session.builder()
                .id(1L)
                .name("Morning Yoga")
                .date(new Date())
                .description("A relaxing morning session")
                .teacher(Teacher.builder().id(1L).lastName("Smith").firstName("Jane").build())
                .users(List.of())
                .build();
    }

    private SessionDto buildDto() {
        SessionDto dto = new SessionDto();
        dto.setId(1L);
        dto.setName("Morning Yoga");
        dto.setDate(new Date());
        dto.setTeacher_id(1L);
        dto.setDescription("A relaxing morning session");
        dto.setUsers(List.of());
        return dto;
    }

    @Test
    @WithMockUser
    void findAll_returnsOk() throws Exception {
        Session session = buildSession();
        SessionDto dto = buildDto();
        when(sessionService.findAll()).thenReturn(List.of(session));
        when(sessionMapper.toDto(List.of(session))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void findById_returnsOk_whenSessionFound() throws Exception {
        Session session = buildSession();
        SessionDto dto = buildDto();
        when(sessionService.getById(1L)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(dto);

        mockMvc.perform(get("/api/session/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning Yoga"));
    }

    @Test
    @WithMockUser
    void findById_returnsNotFound_whenSessionNull() throws Exception {
        when(sessionService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_returnsBadRequest_whenIdNotNumber() throws Exception {
        mockMvc.perform(get("/api/session/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void create_returnsOk() throws Exception {
        Session session = buildSession();
        SessionDto dto = buildDto();
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.create(session)).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(dto);

        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void update_returnsOk() throws Exception {
        Session session = buildSession();
        SessionDto dto = buildDto();
        when(sessionMapper.toEntity(any(SessionDto.class))).thenReturn(session);
        when(sessionService.update(eq(1L), any(Session.class))).thenReturn(session);
        when(sessionMapper.toDto(session)).thenReturn(dto);

        mockMvc.perform(put("/api/session/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void delete_returnsOk_whenSessionFound() throws Exception {
        when(sessionService.getById(1L)).thenReturn(buildSession());

        mockMvc.perform(delete("/api/session/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void delete_returnsNotFound_whenSessionNull() throws Exception {
        when(sessionService.getById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/session/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void participate_returnsOk() throws Exception {
        mockMvc.perform(post("/api/session/1/participate/2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void participate_returnsBadRequest_whenAlreadyParticipating() throws Exception {
        doThrow(new BadRequestException()).when(sessionService).participate(1L, 1L);

        mockMvc.perform(post("/api/session/1/participate/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void noLongerParticipate_returnsOk() throws Exception {
        mockMvc.perform(delete("/api/session/1/participate/2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void noLongerParticipate_returnsBadRequest_whenNotParticipating() throws Exception {
        doThrow(new BadRequestException()).when(sessionService).noLongerParticipate(1L, 1L);

        mockMvc.perform(delete("/api/session/1/participate/1"))
                .andExpect(status().isBadRequest());
    }
}
