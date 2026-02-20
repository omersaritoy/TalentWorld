package com.TalentWorld.backend.service;

import com.TalentWorld.backend.dto.request.JobPostCreateRequest;
import com.TalentWorld.backend.dto.response.JobPostResponse;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;

public interface JobPostService {
    JobPostResponse getJobPosts();
    JobPostResponse getJobPostById(String id);
    JobPostResponse createJobPost(User recurringUser, JobPostCreateRequest request);

}
