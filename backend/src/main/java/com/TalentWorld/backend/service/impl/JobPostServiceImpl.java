package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.JobPostCreateRequest;
import com.TalentWorld.backend.dto.response.JobPostResponse;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.JobPostRepository;
import com.TalentWorld.backend.service.JobPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.TalentWorld.backend.enums.Role.ROLE_RECRUITER;

@Service
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;

    @Override
    public JobPostResponse getJobPosts() {
        return null;
    }

    @Override
    public JobPostResponse getJobPostById(String id) {
        return null;
    }

    @Override
    public JobPostResponse createJobPost(User recurringUser, JobPostCreateRequest request) {
        if (!recurringUser.getRoles().contains(ROLE_RECRUITER)) {
            throw new BusinessException("User Not RECRUITER", "USER_ROLE_NOT_ALLOWED", HttpStatus.FORBIDDEN);
        }
        JobPost jobPost = JobPostCreateRequest.toEntityDto(request);
        jobPost.setUser(recurringUser);

        return JobPostResponse.toDto(jobPostRepository.save(jobPost));

    }
}
