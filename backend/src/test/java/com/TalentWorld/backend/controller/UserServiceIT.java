package com.TalentWorld.backend.controller;


import com.TalentWorld.backend.dto.request.UserUpdate;
import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.excepiton.GlobalExceptionHandler;
import com.TalentWorld.backend.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static com.TalentWorld.backend.enums.Role.ROLE_USER;


import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collections;
import java.util.List;


import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserServiceIT {

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    private UserServiceImpl userService;
    private UserController userController;

    @BeforeEach
    public void setup() {
        userService = Mockito.mock(UserServiceImpl.class);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
        mapper = new ObjectMapper();

    }

    @Test
    void getUsers_ShouldReturnListUserResponse() throws Exception {
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
    void getUsersShouldReturnBusinessExceptionWhenListIsEmpty() throws Exception {
        when(userService.getUsers()).thenThrow(new BusinessException("Users Not Found",
                "USERS_NOT_FOUND",
                HttpStatus.NOT_FOUND));
        mockMvc.perform(get("/api/users"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("Users Not Found")));
    }

    @Test
    void getUserByEmail_ShouldReturnUserResponseWhenEmailFound() throws Exception {
        UserResponse response = new UserResponse("1", "John", "Doe", "john@example.com", true, Collections.singleton(ROLE_USER));
        when(userService.getUserByEmail("john@example.com")).thenReturn(response);

        mockMvc.perform(get("/api/users/getByEmail/john@example.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.id").value("1"));

    }

    @Test
    void getUserByEmail_ShouldReturnBusinessExceptionWhenEmailNotFound() throws Exception {
        when(userService.getUserByEmail("john@example.com")).thenThrow(
                new BusinessException("Users Not Found", "USERS_NOT_FOUND",
                        HttpStatus.NOT_FOUND)
        );

        mockMvc.perform(get("/api/users/getByEmail/john@example.com"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("Users Not Found")));
    }

    @Test
    void getUserByEmail_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        when(userService.getUserByEmail("johnexample.com")).thenThrow(
                new BusinessException("Invalid email format",
                        "INVALID_FORMAT",
                        HttpStatus.BAD_REQUEST)
        );
        mockMvc.perform(get("/api/users/getByEmail/johnexample.com"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", Matchers.containsString("Invalid email format")));
    }

    @Test
    void getActiveUsers_ShouldReturnListOfActiveUsers() throws Exception {
        List<UserResponse> users = List.of(
                new UserResponse("1", "John", "Doe", "john@example.com", true, Collections.singleton(ROLE_USER)),
                new UserResponse("2", "Jane", "Doe", "jane@example.com", true, Collections.singleton(ROLE_USER))
        );
        when(userService.getActiveUsers()).thenReturn(users);
        mockMvc.perform(get("/api/users/activeUsers"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void getActiveUsers_ShouldReturnBusinessExceptionWhenListIsEmpty() throws Exception {
        when(userService.getActiveUsers().isEmpty()).thenThrow(new BusinessException(
                "Active Users Not Found",
                "NOT_FOUND_ACTIVE_USERS",
                HttpStatus.NOT_FOUND
        ));
        mockMvc.perform(get("/api/users/activeUsers"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", Matchers.is("Active Users Not Found")));
    }

    @Test
    void deleteUserById_ShouldReturn200_WhenUserExists() throws Exception {
        String userId = "1";
        String expectedMessage = "User has been deleted by id :" + userId;

        when(userService.deleteUserById(userId)).thenReturn(expectedMessage);

        mockMvc.perform(
                        delete("/api/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedMessage));

        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void deleteUserById_ShouldReturn404_WhenUserNotFound() throws Exception {
        String userId = "999";

        when(userService.deleteUserById(userId))
                .thenThrow(new BusinessException(
                        "User not found with id: " + userId,
                        "USER_ID_NOT_FOUND",
                        HttpStatus.NOT_FOUND));

        mockMvc.perform(
                        delete("/api/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: " + userId));

        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void updateUser_ShouldReturn200_WhenAdminUpdatesUser() throws Exception {
        String userId = "1";
        UserUpdate userUpdate = new UserUpdate("John", "Doe Updated");
        UserResponse expectedResponse = new UserResponse(
                userId, "John", "Doe Updated", "john@example.com",
                true, Collections.singleton(ROLE_USER));

        when(userService.updateUser(any(UserUpdate.class), eq(userId)))
                .thenReturn(expectedResponse);

        mockMvc.perform(
                        patch("/api/users/updateUser/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userUpdate))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.lastName").value("Doe Updated"));

        verify(userService, times(1)).updateUser(any(UserUpdate.class), eq(userId));
    }

    @Test
    void updateUser_ShouldReturn200_WhenOwnerUpdatesOwnProfile() throws Exception {
        String userId = "1";
        UserUpdate userUpdate = new UserUpdate("John", "Doe Updated");
        UserResponse expectedResponse = new UserResponse(
                userId, "John", "Doe Updated", "john@example.com",
                true, Collections.singleton(ROLE_USER));

        when(userService.updateUser(any(UserUpdate.class), eq(userId)))
                .thenReturn(expectedResponse);

        mockMvc.perform(
                        patch("/api/users/updateUser/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userUpdate))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));

        verify(userService, times(1)).updateUser(any(UserUpdate.class), eq(userId));
    }

    @Test
    void updateUser_ShouldReturn403_WhenUserUpdatesOtherProfile() throws Exception {
        String userId = "1";
        UserUpdate userUpdate = new UserUpdate("John", "Doe Updated");

        mockMvc.perform(
                        patch("/api/users/updateUser/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userUpdate))
                )
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    void updateUser_ShouldReturn404_WhenUserNotFound() throws Exception {
        String userId = "999";
        UserUpdate userUpdate = new UserUpdate("John", "Doe");

        when(userService.updateUser(any(UserUpdate.class), eq(userId)))
                .thenThrow(new BusinessException(
                        "User not found with id: " + userId,
                        "USER_ID_NOT_FOUND",
                        HttpStatus.NOT_FOUND));

        mockMvc.perform(
                        patch("/api/users/updateUser/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(userUpdate))
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: " + userId));

        verify(userService, times(1)).updateUser(any(UserUpdate.class), eq(userId));
    }

    @Test
    void changeEmailById_ShouldReturn200_WhenAdminChangesEmail() throws Exception {
        String userId = "1";
        String newEmail = "newemail@example.com";
        UserResponse expectedResponse = new UserResponse(
                userId, "John", "Doe", newEmail,
                true, Collections.singleton(ROLE_USER));

        when(userService.changeEmailById(eq(newEmail), eq(userId)))
                .thenReturn(expectedResponse);

        mockMvc.perform(
                        patch("/api/users/changeEmailById/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newEmail)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail));


    }
    @Test

    void changeEmailById_ShouldReturn200_WhenOwnerChangesOwnEmail() throws Exception {
        String userId = "1";
        String newEmail = "newemail@example.com";
        UserResponse expectedResponse = new UserResponse(
                userId, "John", "Doe", newEmail,
                true, Collections.singleton(ROLE_USER));

        when(userService.changeEmailById(eq(newEmail), eq(userId)))
                .thenReturn(expectedResponse);

        mockMvc.perform(
                       patch("/api/users/changeEmailById/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newEmail)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(newEmail));

        verify(userService, times(1)).changeEmailById(eq(newEmail), eq(userId));
    }


    @Test
    void changeEmailById_ShouldReturn404_WhenUserNotFound() throws Exception {
        String userId = "999";
        String newEmail = "newemail@example.com";

        when(userService.changeEmailById(eq(newEmail), eq(userId)))
                .thenThrow(new BusinessException(
                        "User not found with id: " + userId,
                        "USER_ID_NOT_FOUND",
                        HttpStatus.NOT_FOUND));

        mockMvc.perform(
                       patch("/api/users/changeEmailById/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(newEmail)
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: " + userId));

        verify(userService, times(1)).changeEmailById(eq(newEmail), eq(userId));
    }
}
