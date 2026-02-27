package com.TalentWorld.backend.service;

import com.TalentWorld.backend.dto.request.JobPostCreateRequest;
import com.TalentWorld.backend.dto.request.JobPostUpdateRequest;
import com.TalentWorld.backend.dto.response.JobPostResponse;
import com.TalentWorld.backend.entity.User;

import java.util.List;

public interface JobPostService {
    List<JobPostResponse> getJobPosts();
    JobPostResponse getJobPostById(String id);
    JobPostResponse createJobPost(User recurringUser, JobPostCreateRequest request);
    JobPostResponse updateJobPost(User recurringUser, JobPostUpdateRequest request,String id);
    String deleteJobPostById(String id);

}
