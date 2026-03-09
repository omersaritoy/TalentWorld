package com.TalentWorld.backend.service;

import com.TalentWorld.backend.dto.request.JobApplicationCreateRequest;
import com.TalentWorld.backend.dto.request.JobApplicationStatusUpdateRequest;
import com.TalentWorld.backend.dto.response.JobApplicationResponse;
import com.TalentWorld.backend.dto.response.MyApplicationResponse;
import com.TalentWorld.backend.dto.response.PaginationResponse;
import com.TalentWorld.backend.entity.User;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface JobApplicationService {

    JobApplicationResponse applyToJob(String jobPostId, User talent, JobApplicationCreateRequest request);

    List<JobApplicationResponse> getApplicationsForJob(String jobPostId, User recruiter);

    List<MyApplicationResponse> getMyApplications(User talent);

    JobApplicationResponse updateApplicationStatus(String applicationId,
                                                   User recruiter,
                                                   JobApplicationStatusUpdateRequest request);
    PaginationResponse<JobApplicationResponse> findJobApplicationsWithSort(String filed);
    PaginationResponse<JobApplicationResponse> findJobApplicationsWithPage(int page, int size);
    PaginationResponse<JobApplicationResponse> findApplicationsWithPageAndSort(String filed,int page,int size);
}
