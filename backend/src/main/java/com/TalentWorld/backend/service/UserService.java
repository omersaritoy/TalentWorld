package com.TalentWorld.backend.service;


import com.TalentWorld.backend.dto.response.UserResponse;

import java.util.List;


public interface UserService {

    List<UserResponse> getUsers();
}
