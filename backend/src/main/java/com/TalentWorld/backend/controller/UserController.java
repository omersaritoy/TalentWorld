package com.TalentWorld.backend.controller;


import com.TalentWorld.backend.dto.request.UserUpdate;
import com.TalentWorld.backend.dto.response.PaginationResponse;

import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name="User", description = "User Operations")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Getting users")
    public ResponseEntity<List<UserResponse>> getUsers() {

        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{field}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Getting users with sorted")
    public ResponseEntity<PaginationResponse<UserResponse>> getUsersWithSort(@PathVariable String field) {
        return ResponseEntity.ok(userService.findUserWithShorting(field));
    }

    @GetMapping("/pagination")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users with pagination")
    public ResponseEntity<PaginationResponse<UserResponse>> getUsersWithSort(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(userService.findUsersWithPagination(page, pageSize));
    }

    @GetMapping("/pagination/pageAndSort")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users with pagination and sort")

    public ResponseEntity<PaginationResponse<UserResponse>> getUsersWithSort(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize, @RequestParam String field) {

        return ResponseEntity.ok(userService.findUsersWithPaginationAndSort(page, pageSize, field));
    }

    @GetMapping("/getByEmail/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by email")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {

        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/activeUsers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get active users")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {

        return ResponseEntity.ok(userService.getActiveUsers());
    }

    @GetMapping("/inActiveUsers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get inactive users")
    public ResponseEntity<List<UserResponse>> getInActiveUsers() {

        return ResponseEntity.ok(userService.getInActiveUsers());
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User Deleted successfully"),
            @ApiResponse(responseCode = "404",description = "User not found"),
            @ApiResponse(responseCode = "409",description = "User already deleted")
    })
    public ResponseEntity<String> deleteUserById(@PathVariable String id) {

        return ResponseEntity.ok(userService.deleteUserById(id));
    }

    @PatchMapping("/updateUser/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId==authentication.principal.id")
    @Operation(summary = "User update")

    public ResponseEntity<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdate userUpdate) {

        return ResponseEntity.ok(userService.updateUser(userUpdate, userId));
    }

    @PatchMapping("/changeEmailById/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId==authentication.principal.id")
    @Operation(summary = "User email change")
    public ResponseEntity<UserResponse> changeEmailById(@PathVariable String userId, @RequestBody String email) {

        return ResponseEntity.ok(userService.changeEmailById(email, userId));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        if (currentUser == null)
            throw new BusinessException("User Not Found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(UserResponse.toDto(currentUser));
    }
}
