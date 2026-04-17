package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TeacherController.class)
class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TeacherService teacherService;

    @MockitoBean
    private TeacherMapper teacherMapper;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private Teacher buildTeacher() {
        return Teacher.builder().id(1L).lastName("Smith").firstName("Jane").build();
    }

    private TeacherDto buildDto() {
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setLastName("Smith");
        dto.setFirstName("Jane");
        return dto;
    }

    @Test
    @WithMockUser
    void findAll_returnsOk() throws Exception {
        Teacher teacher = buildTeacher();
        TeacherDto dto = buildDto();
        when(teacherService.findAll()).thenReturn(List.of(teacher));
        when(teacherMapper.toDto(List.of(teacher))).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void findById_returnsOk_whenTeacherFound() throws Exception {
        Teacher teacher = buildTeacher();
        TeacherDto dto = buildDto();
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(dto);

        mockMvc.perform(get("/api/teacher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    @WithMockUser
    void findById_returnsNotFound_whenTeacherNull() throws Exception {
        when(teacherService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/teacher/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_returnsBadRequest_whenIdNotNumber() throws Exception {
        mockMvc.perform(get("/api/teacher/abc"))
                .andExpect(status().isBadRequest());
    }
}
