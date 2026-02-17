package com.TalentWorld.backend.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import com.TalentWorld.backend.dto.request.UserUpdate;
import com.TalentWorld.backend.dto.response.UserResponse;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.UserRepository;
import com.TalentWorld.backend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
        checkPermission(id);//check user roles--> admin or owner
        userUpdate.applyTo(user);

        return UserResponse.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse changeEmailById(String email, String id) {

        checkPermission(id);

        validateEmail(email);

        User user = getUserById(id);

        String normalizedEmail = normalizeEmail(email);

        if (user.getEmail().equals(normalizedEmail)) {
            return UserResponse.toDto(user);
        }

        checkEmailUniqueness(normalizedEmail);

        user.setEmail(normalizedEmail);

        return UserResponse.toDto(user);
    }

    @Override
    public UserResponse getUsrByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException(
                "User not found with email: " + email,
                "USER_NOT_FOUND",
                HttpStatus.NOT_FOUND));

        return UserResponse.toDto(user);
    }


    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(
                    "Email cannot be empty",
                    "EMAIL_EMPTY",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private void checkEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(
                    "Email already in use",
                    "EMAIL_ALREADY_EXISTS",
                    HttpStatus.CONFLICT);
        }
    }

    private User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "User not found with id: " + id,
                        "USER_ID_NOT_FOUND",
                        HttpStatus.NOT_FOUND));
    }

    private void checkPermission(String userId) {
        User currentUser = getUser();


        boolean isAdmin = currentUser.getAuthorities().stream().anyMatch(
                a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN")
        );
        boolean isOwner = currentUser.getId().equals(userId);


        if (!isAdmin && !isOwner) {
            throw new BusinessException(
                    "You are not allowed to change this email",
                    "ACCESS_DENIED",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private static User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            throw new BusinessException(
                    "Unauthorized",
                    "UNAUTHORIZED",
                    HttpStatus.UNAUTHORIZED
            );

        User currentUser = (User) auth.getPrincipal();

        if (currentUser == null)
            throw new BusinessException(
                    "Unauthorized",
                    "UNAUTHORIZED",
                    HttpStatus.UNAUTHORIZED
            );
        return currentUser;
    }

}
