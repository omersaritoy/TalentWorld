package com.TalentWorld.backend.service.impl;


import com.TalentWorld.backend.dto.request.UserUpdate;
import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public List<UserResponse> getActiveUsers() {
        List<User> users = userRepository.findByIsActive(true);
        if (users.isEmpty()) {
            throw new BusinessException("Active Users not found", "Users Not Found", HttpStatus.NOT_FOUND);
        }
        return users.stream().map(UserResponse::toDto).toList();
    }

    @Override
    public List<UserResponse> getInActiveUsers() {
        List<User> users = userRepository.findByIsActive(false);
        if (users.isEmpty()) {
            throw new BusinessException("inactive Users not found", "Users Not Found", HttpStatus.NOT_FOUND);
        }
        return users.stream().map(UserResponse::toDto).toList();
    }

    @Override
    public String deleteUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException("User not found with id: " + id,
                "USER_ID_NOT_FOUND",
                HttpStatus.NOT_FOUND));
        userRepository.delete(user);
        return "User has been deleted by id :" + id;
    }

    @Override
    public UserResponse updateUser(UserUpdate userUpdate, String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new BusinessException("User not found with id: " + id,
                "USER_ID_NOT_FOUND",
                HttpStatus.NOT_FOUND));
        userUpdate.applyTo(user);

        return UserResponse.toDto(userRepository.save(user));
    }

    @Override
    public UserResponse getUsrByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(
                "User not found with email: " + email,
                "USER_NOT_FOUND",
                HttpStatus.NOT_FOUND));

        return UserResponse.toDto(user);
    }
}
