package com.TalentWorld.backend.entity;

import com.TalentWorld.backend.enums.ApplicationStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "job_applications",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"job_post_id", "talent_id"})
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobApplication extends BaseEntity {
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "job_post_id", nullable = false)
        private JobPost jobPost;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "talent_id", nullable = false)
        private User talent;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ApplicationStatus status;

        @Column(length = 1000)
        private String coverLetter;

        @Column(nullable = false)
        private LocalDateTime appliedAt;
}
