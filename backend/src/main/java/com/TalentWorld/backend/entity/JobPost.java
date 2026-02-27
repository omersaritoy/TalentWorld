package com.TalentWorld.backend.entity;

import com.TalentWorld.backend.enums.EmploymentType;
import com.TalentWorld.backend.enums.ExperienceLevel;
import com.TalentWorld.backend.enums.WorkType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
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
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type")
    private EmploymentType employmentType;

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

    @Enumerated(EnumType.STRING)
    @Column(name="work_type")
    private WorkType workType= WorkType.ONSITE;

    private Boolean isActive = true;

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications;

}

