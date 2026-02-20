package com.TalentWorld.backend.repository;

import com.TalentWorld.backend.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostRepository extends JpaRepository<JobPost,String> {
}
