package com.TalentWorld.backend.repository;


import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private User testUser;

    @BeforeEach
    public void setup(){

        testUser = new User();
        testUser.setFirstName("test");
        testUser.setLastName("test");
        testUser.setEmail("test@example.com");
        testUser.setPassword("Test@123");
        testUser.setRoles(Collections.singleton(Role.ROLE_USER));
        testUser.setIsActive(true);
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists(){
        userRepository.save(testUser);
        Optional<User> user = userRepository.findByEmail("test@example.com");
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailNotFound() {
        Optional<User> result = userRepository.findByEmail("notfound@example.com");

        assertThat(result).isEmpty();
    }


}
