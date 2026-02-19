package com.TalentWorld.backend.entity;

import com.TalentWorld.backend.enums.ExperienceLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "job_posts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPost extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="recruiter_id", nullable=false)
    private User user;
    @Column(nullable = false)
    private String title;

    @Column(length = 2000, nullable = false)
    private String description;

    private String location;
    @Enumerated(EnumType.STRING)
    @Column(name = "experience_level")
    private ExperienceLevel experienceLevel;

    @Column(name = "min_experience_year")
    private Integer minExperienceYear;

    @Column(name = "max_experience_year")
    private Integer maxExperienceYear;

    @ElementCollection
    @CollectionTable(
            name = "job_post_skills",
            joinColumns = @JoinColumn(name = "job_post_id")
    )
    @Column(name = "skill")
    private Set<String> skills;

    private Boolean isRemote = false;

    private Boolean isActive = true;

}
