package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.UserRequest;
import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponse saveUser(UserRequest request) {
        User newUser = UserRequest.toUser(request);
        newUser = userRepository.save(newUser);

        return UserResponse.toDto(newUser);
    }
}
