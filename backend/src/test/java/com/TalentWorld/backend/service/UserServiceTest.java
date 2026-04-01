package com.TalentWorld.backend.service;


import com.TalentWorld.backend.dto.request.UserUpdate;
import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;

import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.http.HttpStatus;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowableOfType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;


    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getUsers_ShouldReturnUserList_WhenUsersExist() {
        User user1 = new User("John", "Doe", "john@example.com", "Password1@", Set.of(Role.ROLE_USER));
        User user2 = new User("Jane", "Doe", "jane@example.com", "Password1@", Set.of(Role.ROLE_USER));

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserResponse> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();

    }

    @Test
    void getUsers_ShouldThrowBusinessException_WhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        assertThatThrownBy(() -> userService.getUsers()).hasMessage("Users not found");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getActiveUsers_ShouldReturnUserList_WhenUsersExist() {
        User user1 = new User("John", "Doe", "john@example.com", "Password1@", Set.of(Role.ROLE_USER));
        User user2 = new User("Jane", "Doe", "jane@example.com", "Password1@", Set.of(Role.ROLE_USER));
        user1.setIsActive(true);
        user2.setIsActive(true);
        when(userRepository.findByIsActive(true)).thenReturn(List.of(user1, user2));
        List<UserResponse> result = userService.getActiveUsers();
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findByIsActive(true);
    }

    @Test
    void getActiveUsers_ShouldThrowBusinessException_WhenNoActiveUsersExist() {
        when(userRepository.findByIsActive(true)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> userService.getActiveUsers())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Active Users not found");

        verify(userRepository, times(1)).findByIsActive(true);
    }

    @Test
    void deleteUserById_ShouldDeleteUserById() {
        User user = new User("John", "Doe", "john@example.com", "Password1@", Set.of(Role.ROLE_USER));
        user.setIsActive(true);
        when(userRepository.findById("test-1234")).thenReturn(Optional.of(user));
        String result = userService.deleteUserById("test-1234");
        assertEquals("User has been deleted by id :test-1234", result);
        assertFalse(user.getIsActive());
        verify(userRepository, times(1)).findById("test-1234");
    }

    @Test
    void deleteUserById_ShouldThrowBusinessException_WhenNoActiveUsersExist() {
        when(userRepository.findById("test-1234")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.deleteUserById("test-1234"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User not found with id: test-1234");
        verify(userRepository, times(1)).findById("test-1234");
    }

    @Test
    void deleteUserById_ShouldThrowBusinessException_WithNotFoundStatus() {
        when(userRepository.findById("test-1234")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUserById("test-1234"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("User not found with id: test-1234");
        verify(userRepository, times(1)).findById("test-1234");

    }

}



