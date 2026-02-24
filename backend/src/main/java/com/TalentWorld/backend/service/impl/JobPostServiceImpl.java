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

import java.util.List;
import java.util.stream.Collectors;

import static com.TalentWorld.backend.enums.Role.ROLE_RECRUITER;

@Service
@RequiredArgsConstructor
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;

    @Override
    public List<JobPostResponse> getJobPosts() {
        List<JobPost> jobPosts = jobPostRepository.findAll();

        return jobPosts.stream().map(JobPostResponse::toDto).collect(Collectors.toList());
    }

    @Override
    public JobPostResponse getJobPostById(String id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> new BusinessException("Job post not found with id: " + id,
                "JOB_POST_NOT_FOUND",
                HttpStatus.NOT_FOUND
        ));

        return JobPostResponse.toDto(jobPost);
    }

    @Override
    public JobPostResponse createJobPost(User recurringUser, JobPostCreateRequest request) {
        if (!recurringUser.getRoles().contains(ROLE_RECRUITER)) {
            throw new BusinessException("User Not RECRUITER", "USER_ROLE_NOT_ALLOWED", HttpStatus.FORBIDDEN);
        }
        if (request.maxExperienceYear() < request.minExperienceYear()) {
            throw new BusinessException("Min experience year can not be bigger than max experience year", "MAX_EXPERIENCE_YEAR", HttpStatus.BAD_REQUEST);
        }
        JobPost jobPost = JobPostCreateRequest.toEntityDto(request);
        jobPost.setUser(recurringUser);

        return JobPostResponse.toDto(jobPostRepository.save(jobPost));
    }

    @Override
    public String deleteJobPostById(String id) {
        JobPost jobPost=jobPostRepository.findById(id).orElseThrow(()->new BusinessException(
                "Job post not found with id: " + id, "JOB_POST_NOT_FOUND",
                HttpStatus.NOT_FOUND
        ));
        jobPostRepository.delete(jobPost);
        return "Post deleted successfully with id: " + id;
    }
}
