package com.TalentWorld.backend.service;

import com.TalentWorld.backend.dto.request.JobApplicationCreateRequest;
import com.TalentWorld.backend.dto.request.JobApplicationStatusUpdateRequest;
import com.TalentWorld.backend.dto.response.JobApplicationResponse;
import com.TalentWorld.backend.dto.response.MyApplicationResponse;
import com.TalentWorld.backend.entity.User;

import java.util.List;

public interface JobApplicationService {

    JobApplicationResponse applyToJob(String jobPostId, User talent, JobApplicationCreateRequest request);

    List<JobApplicationResponse> getApplicationsForJob(String jobPostId, User recruiter);

    List<MyApplicationResponse> getMyApplications(User talent);

    JobApplicationResponse updateApplicationStatus(String applicationId,
                                                   User recruiter,
                                                   JobApplicationStatusUpdateRequest request);
}
