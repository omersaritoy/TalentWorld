package com.TalentWorld.backend.controller;


import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.service.impl.AuthService;
import com.TalentWorld.backend.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static com.TalentWorld.backend.enums.Role.ROLE_USER;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerIT {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UserServiceImpl userService;
    private UserController userController;

    @BeforeEach
    void setup() {
        userService = Mockito.mock(UserServiceImpl.class);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getUsers_ShouldReturnUserList() throws Exception {
        List<UserResponse> users = List.of(
                new UserResponse("1", "John", "Doe", "john@example.com", true, Collections.singleton(ROLE_USER)),
                new UserResponse("2", "Jane", "Doe", "jane@example.com", true, Collections.singleton(ROLE_USER))
        );
        when(userService.getUsers()).thenReturn(users);
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getUserByEmail_ShouldReturnUser() throws Exception {
        UserResponse response = new UserResponse(
                "1", "John", "Doe", "john@example.com",true, Collections.singleton(ROLE_USER)
        );

        when(userService.getUserByEmail("john@example.com"))
                .thenReturn(response);

        mockMvc.perform(get("/api/users/getByEmail/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

}
