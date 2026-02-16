package com.TalentWorld.backend.controller;

import com.TalentWorld.backend.dto.request.UserUpdate;
import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/getByEmail/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUsrByEmail(email));
    }

    @GetMapping("/activeUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/inActiveUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getInActiveUsers() {
        return ResponseEntity.ok(userService.getInActiveUsers());
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUserById(@RequestParam String id) {
        return ResponseEntity.ok(userService.deleteUserById(id));
    }

    @PatchMapping("/updateUser/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId==authentication.principal.id")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdate userUpdate) {
        return ResponseEntity.ok(userService.updateUser(userUpdate, userId));
    }

    @PatchMapping("/changeEmailById/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId==authentication.principal.id")
    public ResponseEntity<UserResponse> changeEmailById(@PathVariable String userId, @RequestBody String email) {
        return ResponseEntity.ok(userService.changeEmailById(email, userId));
    }
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        if(currentUser==null)
            throw new BusinessException("User Not Found","USER_NOT_FOUND", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(UserResponse.toDto(currentUser));
    }

}
