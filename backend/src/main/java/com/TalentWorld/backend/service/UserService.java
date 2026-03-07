package com.TalentWorld.backend.service;




import com.TalentWorld.backend.dto.request.UserUpdate;

import com.TalentWorld.backend.dto.response.PaginationResponse;

import com.TalentWorld.backend.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;

import java.util.List;


public interface UserService {

    List<UserResponse> getUsers();
    UserResponse getUsrByEmail(String email);
    List<UserResponse> getActiveUsers();
    List<UserResponse> getInActiveUsers();
    String deleteUserById(String id);
    UserResponse updateUser(UserUpdate userUpdate, String id);
    UserResponse changeEmailById(String email, String id);
    PaginationResponse<UserResponse> findUserWithShorting(String field);
    PaginationResponse<UserResponse> findUsersWithPagination(int page, int pageSize);
    PaginationResponse<UserResponse> findUsersWithPaginationAndSort(int page, int pageSize, String field);
}


