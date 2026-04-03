package com.TalentWorld.backend.service;

import com.TalentWorld.backend.dto.request.JobPostCreateRequest;
import com.TalentWorld.backend.dto.request.JobPostUpdateRequest;
import com.TalentWorld.backend.dto.response.JobPostResponse;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.EmploymentType;
import com.TalentWorld.backend.enums.ExperienceLevel;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.enums.WorkType;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.JobPostRepository;
import com.TalentWorld.backend.service.impl.JobPostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class JobPostServiceTest {
    private JobPostRepository jobPostRepository;

    private JobPostServiceImpl jobPostService;

    private JobPost jobPost;
    private JobPostCreateRequest request;
    private User recruiterUser;

    @BeforeEach
    void setUp() {
        jobPostRepository = Mockito.mock(JobPostRepository.class);
        jobPostService = new JobPostServiceImpl(jobPostRepository);

        request = new JobPostCreateRequest(
                "title", "description", "location",
                WorkType.HYBRID, EmploymentType.FULL_TIME, ExperienceLevel.JUNIOR,
                2, 4,
                Set.of("Java", "Spring Boot", "Sql"));


        recruiterUser = new User();
        recruiterUser.setId("user-123");
        recruiterUser.setRoles(Collections.singleton(Role.ROLE_RECRUITER));

        jobPost = JobPostCreateRequest.toEntityDto(request);
        jobPost.setUser(recruiterUser);
        jobPost.setId("test-123");
    }

    @Test
    void createJobPost_ShouldReturnJobPostResponse_WhenUserIsRecruiter() {
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(jobPost);

        JobPostResponse response=jobPostService.createJobPost(recruiterUser, request);
        assertNotNull(response);
        assertThat(response.title()).isEqualTo(request.title());
        verify(jobPostRepository).save(any(JobPost.class));
    }

    @Test
    void createJobPost_ShouldThrowBusinessException_WhenUserIsNotRecruiter() {
        User nonRecruiter = new User();
        nonRecruiter.setId("user-456");
        nonRecruiter.setRoles(Collections.singleton(Role.ROLE_USER));

        assertThrows(BusinessException.class,
                () -> jobPostService.createJobPost(nonRecruiter, request));

        verify(jobPostRepository, never()).save(any());
    }
    @Test
    void createJobPost_ShouldThrowBusinessException_WhenMinExperienceYearBiggerThanMax() {
        JobPostCreateRequest invalidRequest = new JobPostCreateRequest(
                "title", "description", "location",
                WorkType.HYBRID, EmploymentType.FULL_TIME, ExperienceLevel.JUNIOR,
                5, 2,
                Set.of("Java"));

        assertThrows(BusinessException.class,
                () -> jobPostService.createJobPost(recruiterUser, invalidRequest));

        verify(jobPostRepository, never()).save(any());
    }
    @Test
    void updateJobPost_ShouldReturnUpdatedResponse_WhenUserIsOwner() {
        when(jobPostRepository.findById(jobPost.getId())).thenReturn(Optional.of(jobPost));
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(jobPost);
        JobPostUpdateRequest updateRequest = new JobPostUpdateRequest("title", "description", "location",
                WorkType.HYBRID, EmploymentType.FULL_TIME, ExperienceLevel.JUNIOR,
                5, 2,
                Set.of("Java"),true);

        JobPostResponse response = jobPostService.updateJobPost(recruiterUser, updateRequest, jobPost.getId());

        assertNotNull(response);
        assertThat(response.id()).isEqualTo(jobPost.getId());
        verify(jobPostRepository).findById(jobPost.getId());
        verify(jobPostRepository).save(any(JobPost.class));
    }
    @Test
    void updateJobPost_ShouldReturnUpdatedResponse_WhenUserIsAdmin() {
        User adminUser = new User();
        adminUser.setId("admin-123");
        adminUser.setRoles(Collections.singleton(Role.ROLE_ADMIN));
        JobPostUpdateRequest updateRequest = new JobPostUpdateRequest("title", "description", "location",
                WorkType.HYBRID, EmploymentType.FULL_TIME, ExperienceLevel.JUNIOR,
                5, 2,
                Set.of("Java"),true);
        when(jobPostRepository.findById(jobPost.getId())).thenReturn(Optional.of(jobPost));
        when(jobPostRepository.save(any(JobPost.class))).thenReturn(jobPost);

        JobPostResponse response = jobPostService.updateJobPost(adminUser, updateRequest, jobPost.getId());

        assertNotNull(response);
        verify(jobPostRepository).save(any(JobPost.class));
    }

    @Test
    void updateJobPost_ShouldThrowBusinessException_WhenJobPostNotFound() {
        when(jobPostRepository.findById(jobPost.getId())).thenReturn(Optional.empty());
        JobPostUpdateRequest updateRequest = new JobPostUpdateRequest("title", "description", "location",
                WorkType.HYBRID, EmploymentType.FULL_TIME, ExperienceLevel.JUNIOR,
                5, 2,
                Set.of("Java"),true);
        assertThrows(BusinessException.class,
                () -> jobPostService.updateJobPost(recruiterUser, updateRequest, jobPost.getId()));

        verify(jobPostRepository, never()).save(any());
    }
    @Test
    void updateJobPost_ShouldThrowBusinessException_WhenUserIsNotOwnerOrAdmin() {
        User otherUser = new User();
        otherUser.setId("other-456");
        otherUser.setRoles(Collections.singleton(Role.ROLE_RECRUITER));
        JobPostUpdateRequest updateRequest = new JobPostUpdateRequest("title", "description", "location",
                WorkType.HYBRID, EmploymentType.FULL_TIME, ExperienceLevel.JUNIOR,
                5, 2,
                Set.of("Java"),true);
        when(jobPostRepository.findById(jobPost.getId())).thenReturn(Optional.of(jobPost));

        assertThrows(BusinessException.class,
                () -> jobPostService.updateJobPost(otherUser, updateRequest, jobPost.getId()));

        verify(jobPostRepository, never()).save(any());
    }
    @Test
    void deleteJobPostById_ShouldReturnSuccessMessage_WhenJobPostExists() {
        when(jobPostRepository.findById(jobPost.getId())).thenReturn(Optional.of(jobPost));

        String result = jobPostService.deleteJobPostById(jobPost.getId());

        assertThat(result).isEqualTo("Post deleted successfully with id: " + jobPost.getId());
        verify(jobPostRepository).findById(jobPost.getId());
        verify(jobPostRepository).delete(jobPost);
    }


    @Test
    void deleteJobPostById_ShouldThrowBusinessException_WhenJobPostNotFound() {
        when(jobPostRepository.findById(jobPost.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> jobPostService.deleteJobPostById(jobPost.getId()));

        verify(jobPostRepository, never()).delete(any());
    }
    @Test
    void getJobPostById_ShouldReturnJobPostResponseWhenExists() {
        when(jobPostRepository.findById(jobPost.getId()))
                .thenReturn(Optional.of(jobPost));

        JobPostResponse response = jobPostService.getJobPostById(jobPost.getId());

        assertNotNull(response);
        assertThat(response.id()).isEqualTo(jobPost.getId());
        assertThat(response.title()).isEqualTo(jobPost.getTitle());
        assertThat(response.description()).isEqualTo(jobPost.getDescription());
        assertThat(response.location()).isEqualTo(jobPost.getLocation());
        assertThat(response.workType()).isEqualTo(jobPost.getWorkType());
        assertThat(response.employmentType()).isEqualTo(jobPost.getEmploymentType());
        assertThat(response.experienceLevel()).isEqualTo(jobPost.getExperienceLevel());

        verify(jobPostRepository).findById(jobPost.getId());
    }

    @Test
    void getJobPostById_ShouldThrowBusinessException_WhenJobPostNotFound() {
        when(jobPostRepository.findById(jobPost.getId())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class,
                () -> jobPostService.getJobPostById(jobPost.getId()));

        verify(jobPostRepository).findById(jobPost.getId());
    }
}
