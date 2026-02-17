package com.TalentWorld.backend.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "talent_profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TalentProfile extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String title;
    @Column(name = "experience_year")
    private Integer experienceYear;

    @Column(length = 1000)
    private String about;

    @ElementCollection
    @CollectionTable(
            name = "talent_skills",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    private Set<String> skills;

}
