package com.TalentWorld.backend.service;


import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;

import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;


import java.util.List;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;


    @BeforeEach
    public void setup() {
        userRepository= Mockito.mock(UserRepository.class);
        userService=new UserServiceImpl(userRepository);
    }

    @Test
    public void getUsers_ShouldReturnUserList_WhenUsersExist() {
        User user1 = new User("John", "Doe", "john@example.com", "Password1@", Set.of(Role.ROLE_USER));
        User user2 = new User("Jane", "Doe", "jane@example.com", "Password1@", Set.of(Role.ROLE_USER));

        when(userRepository.findAll()).thenReturn(List.of(user1,user2));
        List<UserResponse> result=userService.getUsers();

        assertNotNull(result);
        assertEquals(2,result.size());
        verify(userRepository, times(1)).findAll();


    }
}
