package com.openclassrooms.starterjwt.controllers;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private User buildUser() {
        return new User("user@test.com", "Doe", "John", "encoded", false);
    }

    private UserDto buildDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("user@test.com");
        dto.setLastName("Doe");
        dto.setFirstName("John");
        dto.setAdmin(false);
        return dto;
    }

    @Test
    @WithMockUser
    void findById_returnsOk_whenUserFound() throws Exception {
        User user = buildUser();
        UserDto dto = buildDto();
        when(userService.findById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    @WithMockUser
    void findById_returnsNotFound_whenUserNull() throws Exception {
        when(userService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/user/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void findById_returnsBadRequest_whenIdNotNumber() throws Exception {
        mockMvc.perform(get("/api/user/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    void delete_returnsOk_whenOwner() throws Exception {
        User user = buildUser();
        when(userService.findById(1L)).thenReturn(user);
        when(userService.isOwner(1L, "user@test.com")).thenReturn(true);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "other@test.com")
    void delete_returnsUnauthorized_whenNotOwner() throws Exception {
        User user = buildUser();
        when(userService.findById(1L)).thenReturn(user);
        when(userService.isOwner(1L, "other@test.com")).thenReturn(false);

        mockMvc.perform(delete("/api/user/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void delete_returnsNotFound_whenUserNull() throws Exception {
        when(userService.findById(999L)).thenReturn(null);

        mockMvc.perform(delete("/api/user/999"))
                .andExpect(status().isNotFound());
    }
}
