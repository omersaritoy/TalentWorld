package com.TalentWorld.backend.service;


import com.TalentWorld.backend.dto.request.UserUpdate;
import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;

import java.util.List;


public interface UserService {

    List<UserResponse> getUsers();
    UserResponse getUsrByEmail(String email);
    List<UserResponse> getActiveUsers();
    List<UserResponse> getInActiveUsers();
    String deleteUserById(String id);
    UserResponse updateUser(UserUpdate userUpdate, String id);
}
