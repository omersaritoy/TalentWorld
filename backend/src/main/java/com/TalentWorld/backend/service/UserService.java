package com.TalentWorld.backend.service;


import com.TalentWorld.backend.dto.request.UserRequest;
import com.TalentWorld.backend.dto.response.UserResponse;


public interface UserService {
        UserResponse saveUser(UserRequest request);
}
