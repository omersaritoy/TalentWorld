package com.TalentWorld.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name="is_active")
    private Boolean isActive=false;
    public User(String firstName, String lastName, String email, Boolean isActive) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
