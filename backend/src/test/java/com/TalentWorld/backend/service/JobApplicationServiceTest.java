package com.TalentWorld.backend.service;

import com.TalentWorld.backend.dto.request.JobApplicationCreateRequest;
import com.TalentWorld.backend.dto.request.JobApplicationStatusUpdateRequest;
import com.TalentWorld.backend.dto.response.JobApplicationResponse;
import com.TalentWorld.backend.dto.response.MyApplicationResponse;
import com.TalentWorld.backend.entity.JobApplication;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.ApplicationStatus;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.JobApplicationRepository;
import com.TalentWorld.backend.repository.JobPostRepository;
import com.TalentWorld.backend.service.impl.JobApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.TalentWorld.backend.enums.Role.ROLE_RECRUITER;
import static com.TalentWorld.backend.enums.Role.ROLE_USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class JobApplicationServiceTest {
    private JobApplicationRepository jobApplicationRepository;
    private JobPostRepository jobPostRepository;
    private JobApplicationServiceImpl jobApplicationService;


    private User talentUser;
    private User nonTalentUser;
    private User recruiterUser;
    private JobPost activeJobPost;
    private JobPost inactiveJobPost;
    private JobApplicationCreateRequest request;


    @BeforeEach
    void setUp() {
        jobApplicationRepository = Mockito.mock(JobApplicationRepository.class);
        jobPostRepository = Mockito.mock(JobPostRepository.class);
        jobApplicationService = new JobApplicationServiceImpl(jobApplicationRepository, jobPostRepository);


        talentUser = new User();
        talentUser.setId("user-1");
        talentUser.setEmail("talent@example.com");
        talentUser.setRoles(Collections.singleton(ROLE_USER));

        recruiterUser = new User();
        recruiterUser.setId("user-2");
        recruiterUser.setRoles(Collections.singleton(Role.ROLE_RECRUITER));

        nonTalentUser = new User();
        nonTalentUser.setId("user-2");
        nonTalentUser.setEmail("employer@example.com");


        activeJobPost = new JobPost();
        activeJobPost.setId("job-1");
        activeJobPost.setTitle("Backend Developer");
        activeJobPost.setIsActive(true);


        inactiveJobPost = new JobPost();
        inactiveJobPost.setId("job-2");
        inactiveJobPost.setTitle("Archived Role");
        inactiveJobPost.setIsActive(false);

        request = new JobApplicationCreateRequest("I am a great fit for this role.");
    }

    @Test
    void applyToJob_ShouldReturnJobApplicationResponse_WhenIsUserIsTalent() {
        when(jobPostRepository.findById(activeJobPost.getId())).thenReturn(Optional.of(activeJobPost));
        when(jobApplicationRepository.existsByJobPostAndTalent(activeJobPost, talentUser)).thenReturn(false);

        JobApplication savedApplication = new JobApplication();
        savedApplication.setId("app-1");
        savedApplication.setJobPost(activeJobPost);
        savedApplication.setTalent(talentUser);
        savedApplication.setStatus(ApplicationStatus.APPLIED);
        savedApplication.setCoverLetter(request.coverLetter());
        savedApplication.setAppliedAt(LocalDateTime.now());

        when(jobApplicationRepository.save(any(JobApplication.class))).thenReturn(savedApplication);

        JobApplicationResponse response = jobApplicationService.applyToJob(activeJobPost.getId(), talentUser, request);

        assertNotNull(response);
        assertThat(response.jobPostId()).isEqualTo(activeJobPost.getId());
        assertThat(response.talentId()).isEqualTo(talentUser.getId());
        verify(jobApplicationRepository).save(any(JobApplication.class));

    }

    @Test
    void applyToJob_ShouldThrowBusinessException_WhenUserIsNotTalent() {
        nonTalentUser.setRoles(Collections.singleton(ROLE_RECRUITER));
        assertThrows(BusinessException.class,
                ()->jobApplicationService.applyToJob(activeJobPost.getId(), nonTalentUser, request));
        verify(jobApplicationRepository,never()).save(any());
    }
    @Test
    void applyToJob_ShouldThrowBusinessException_WhenJobPostNotFound() {
        when(jobPostRepository.findById(activeJobPost.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> jobApplicationService.applyToJob(activeJobPost.getId(), talentUser, request));

        verify(jobApplicationRepository, never()).save(any());
    }

    @Test
    void applyToJob_ShouldThrowBusinessException_WhenJobPostIsInactive() {
        when(jobPostRepository.findById(inactiveJobPost.getId())).thenReturn(Optional.of(inactiveJobPost));

        assertThrows(BusinessException.class,
                () -> jobApplicationService.applyToJob(inactiveJobPost.getId(), talentUser, request));

        verify(jobApplicationRepository, never()).save(any());
    }

    @Test
    void applyToJob_ShouldThrowBusinessException_WhenAlreadyApplied() {
        when(jobPostRepository.findById(activeJobPost.getId())).thenReturn(Optional.of(activeJobPost));
        when(jobApplicationRepository.existsByJobPostAndTalent(activeJobPost, talentUser)).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> jobApplicationService.applyToJob(activeJobPost.getId(), talentUser, request));

        verify(jobApplicationRepository, never()).save(any());
    }
    @Test
    void getApplicationsForJob_ShouldReturnApplicationList_WhenUserIsOwner() {
        JobApplication application = new JobApplication();
        application.setId("app-1");
        application.setJobPost(activeJobPost);
        application.setTalent(talentUser);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());

        activeJobPost.setUser(recruiterUser); // owner

        when(jobPostRepository.findById(activeJobPost.getId())).thenReturn(Optional.of(activeJobPost));
        when(jobApplicationRepository.findByJobPost(activeJobPost)).thenReturn(List.of(application));

        List<JobApplicationResponse> responses = jobApplicationService
                .getApplicationsForJob(activeJobPost.getId(), recruiterUser);

        assertNotNull(responses);
        assertThat(responses.size()).isEqualTo(1);
        verify(jobApplicationRepository).findByJobPost(activeJobPost);
    }

    @Test
    void getApplicationsForJob_ShouldReturnApplicationList_WhenUserIsAdmin() {
        User adminUser = new User();
        adminUser.setId("admin-1");
        adminUser.setRoles(Collections.singleton(Role.ROLE_ADMIN));

        activeJobPost.setUser(recruiterUser);

        when(jobPostRepository.findById(activeJobPost.getId())).thenReturn(Optional.of(activeJobPost));
        when(jobApplicationRepository.findByJobPost(activeJobPost)).thenReturn(List.of());

        List<JobApplicationResponse> responses = jobApplicationService
                .getApplicationsForJob(activeJobPost.getId(), adminUser);

        assertNotNull(responses);
        verify(jobApplicationRepository).findByJobPost(activeJobPost);
    }

    @Test
    void getApplicationsForJob_ShouldThrowBusinessException_WhenJobPostNotFound() {
        when(jobPostRepository.findById(activeJobPost.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> jobApplicationService.getApplicationsForJob(activeJobPost.getId(), recruiterUser));

        verify(jobApplicationRepository, never()).findByJobPost(any());
    }

    @Test
    void getApplicationsForJob_ShouldThrowBusinessException_WhenUserIsNotOwnerOrAdmin() {
        User otherUser = new User();
        otherUser.setId("other-1");
        otherUser.setRoles(Collections.singleton(Role.ROLE_RECRUITER));

        activeJobPost.setUser(recruiterUser); // owner farklı biri

        when(jobPostRepository.findById(activeJobPost.getId())).thenReturn(Optional.of(activeJobPost));

        assertThrows(BusinessException.class,
                () -> jobApplicationService.getApplicationsForJob(activeJobPost.getId(), otherUser));

        verify(jobApplicationRepository, never()).findByJobPost(any());
    }
    @Test
    void getMyApplications_ShouldReturnApplicationList_WhenUserIsTalent() {
        JobApplication application = new JobApplication();
        application.setId("app-1");
        application.setJobPost(activeJobPost);
        application.setTalent(talentUser);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());

        when(jobApplicationRepository.findByTalent(talentUser)).thenReturn(List.of(application));

        List<MyApplicationResponse> responses = jobApplicationService.getMyApplications(talentUser);

        assertNotNull(responses);
        assertThat(responses.size()).isEqualTo(1);
        verify(jobApplicationRepository).findByTalent(talentUser);
    }

    @Test
    void getMyApplications_ShouldReturnEmptyList_WhenUserHasNoApplications() {
        when(jobApplicationRepository.findByTalent(talentUser)).thenReturn(List.of());

        List<MyApplicationResponse> responses = jobApplicationService.getMyApplications(talentUser);

        assertNotNull(responses);
        assertThat(responses.size()).isEqualTo(0);
        verify(jobApplicationRepository).findByTalent(talentUser);
    }

    @Test
    void getMyApplications_ShouldThrowBusinessException_WhenUserIsNotTalent() {
        nonTalentUser.setRoles(Collections.singleton(Role.ROLE_RECRUITER));

        assertThrows(BusinessException.class,
                () -> jobApplicationService.getMyApplications(nonTalentUser));

        verify(jobApplicationRepository, never()).findByTalent(any());
    }
    @Test
    void updateApplicationStatus_ShouldReturnUpdatedResponse_WhenUserIsOwner() {
        JobApplication application = new JobApplication();
        application.setId("app-1");
        application.setJobPost(activeJobPost);
        application.setTalent(talentUser);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());

        activeJobPost.setUser(recruiterUser);

        JobApplicationStatusUpdateRequest statusRequest =
                new JobApplicationStatusUpdateRequest(ApplicationStatus.APPLIED);

        when(jobApplicationRepository.findById("app-1")).thenReturn(Optional.of(application));

        JobApplicationResponse response = jobApplicationService
                .updateApplicationStatus("app-1", recruiterUser, statusRequest);

        assertNotNull(response);
        assertThat(response.status()).isEqualTo(ApplicationStatus.APPLIED);
        verify(jobApplicationRepository).findById("app-1");
    }

    @Test
    void updateApplicationStatus_ShouldReturnUpdatedResponse_WhenUserIsAdmin() {
        User adminUser = new User();
        adminUser.setId("admin-1");
        adminUser.setRoles(Collections.singleton(Role.ROLE_ADMIN));

        JobApplication application = new JobApplication();
        application.setId("app-1");
        application.setJobPost(activeJobPost);
        application.setTalent(talentUser);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());

        activeJobPost.setUser(recruiterUser);

        JobApplicationStatusUpdateRequest statusRequest =
                new JobApplicationStatusUpdateRequest(ApplicationStatus.REJECTED);

        when(jobApplicationRepository.findById("app-1")).thenReturn(Optional.of(application));

        JobApplicationResponse response = jobApplicationService
                .updateApplicationStatus("app-1", adminUser, statusRequest);

        assertNotNull(response);
        assertThat(response.status()).isEqualTo(ApplicationStatus.REJECTED);
    }

    @Test
    void updateApplicationStatus_ShouldThrowBusinessException_WhenApplicationNotFound() {
        JobApplicationStatusUpdateRequest statusRequest =
                new JobApplicationStatusUpdateRequest(ApplicationStatus.APPLIED);

        when(jobApplicationRepository.findById("app-1")).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> jobApplicationService.updateApplicationStatus("app-1", recruiterUser, statusRequest));
    }

    @Test
    void updateApplicationStatus_ShouldThrowBusinessException_WhenUserIsNotOwnerOrAdmin() {
        User otherUser = new User();
        otherUser.setId("other-1");
        otherUser.setRoles(Collections.singleton(Role.ROLE_RECRUITER));

        JobApplication application = new JobApplication();
        application.setId("app-1");
        application.setJobPost(activeJobPost);
        application.setTalent(talentUser);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());

        activeJobPost.setUser(recruiterUser);

        JobApplicationStatusUpdateRequest statusRequest =
                new JobApplicationStatusUpdateRequest(ApplicationStatus.APPLIED);

        when(jobApplicationRepository.findById("app-1")).thenReturn(Optional.of(application));

        assertThrows(BusinessException.class,
                () -> jobApplicationService.updateApplicationStatus("app-1", otherUser, statusRequest));
    }
}
