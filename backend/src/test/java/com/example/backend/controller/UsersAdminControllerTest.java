package com.example.backend.controller;

import com.example.backend.dto.users.AdminUserUpdateDTO;
import com.example.backend.dto.users.UserUpdateDTOBase;
import com.example.backend.dto.users.UserUpdateVO;
import com.example.backend.entity.Users;
import com.example.backend.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsersAdminControllerTest {

    @Mock
    private UsersService mockUsersService;

    @InjectMocks
    private UsersAdminController usersAdminController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(usersAdminController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                //.apply(springSecurity())
                .build();
    }

    @Test
    void getStatus_returnsList() throws Exception {
        // Arrange
        final UserUpdateVO user = new UserUpdateVO();
        user.setUserId("userId");
        user.setFirstName("firstName");
        user.setEmail("email");
        user.setRole("role");
        final List<UserUpdateVO> users = List.of(user);
        when(mockUsersService.list()).thenReturn((List) users);

        // Act & Assert
        mockMvc.perform(get("/api/admin/users/status")
                        .with(user("admin").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getStatus_emptyList_returnsOk() throws Exception {
        when(mockUsersService.list()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/users/status")
                        .with(user("admin").roles("ADMIN"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllUsers_returnsOk() throws Exception {
        final UserUpdateVO user = new UserUpdateVO();
        user.setUserId("userId");
        when(mockUsersService.list()).thenReturn((List) List.of(user));

        mockMvc.perform(get("/api/admin/users")
                        .with(user("admin").roles("ADMIN"))
                        .param("page", "0")
                        .param("size", "10")
                        .param("keyword", "kw")
                        .param("role", "role")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateUserByAdmin_success_returnsOk() throws Exception {
        AdminUserUpdateDTO req = new AdminUserUpdateDTO();
        req.setUserId("userId");
        req.setRole("ADMIN");
        req.setEmail("different@email.com");
        req.setFirstKana("Different");
        req.setLastKana("Different");
        req.setFirstName("Different");
        req.setLastName("Different");
        req.setStatus("Active");
        UserUpdateVO vo = new UserUpdateVO();
        vo.setUserId("userId");
        vo.setFirstName("firstName");
        vo.setEmail("email");
        vo.setRole("role");

        when(mockUsersService.updateUser(any(AdminUserUpdateDTO.class))).thenReturn(vo);

        mockMvc.perform(put("/api/admin/users")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("userId")));
    }

/*

    @Test
    void updateUserByAdmin_nullPathUserId_returnsBadRequest() throws Exception {
        AdminUserUpdateDTO req = new AdminUserUpdateDTO();
        req.setUserId("someUserId");

        mockMvc.perform(put("/api/admin/users/{userId}", (String) null)
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
*/

    @Test
    void updateUserByAdmin_nullRequestBodyUserId_returnsBadRequest() throws Exception {
        AdminUserUpdateDTO req = new AdminUserUpdateDTO();
        // req.setUserId(null); // userId 为 null

        mockMvc.perform(put("/api/admin/users")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
