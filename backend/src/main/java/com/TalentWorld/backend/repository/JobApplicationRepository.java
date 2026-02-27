package com.TalentWorld.backend.repository;

import com.TalentWorld.backend.entity.JobApplication;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication,String> {

    boolean existsByJobPostAndTalent(JobPost jobPost, User talent);

    List<JobApplication> findByJobPost(JobPost jobPost);

    List<JobApplication> findByTalent(User talent);}
