package com.TalentWorld.backend.service.impl;


import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserResponse::toDto).toList();
    }

    @Override
    public UserResponse getUsrByEmail(String email) {
        User user=userRepository.findByEmail(email).orElseThrow(()->new BusinessException(
                "User not found with email: " + email,
                "USER_NOT_FOUND",
                HttpStatus.NOT_FOUND));

        return UserResponse.toDto(user);
    }
}
