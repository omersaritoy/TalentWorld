package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.*;
import com.TalentWorld.backend.dto.response.*;
import com.TalentWorld.backend.entity.JobApplication;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.ApplicationStatus;

import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.*;

import com.TalentWorld.backend.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.*;
import java.time.LocalDateTime;
import java.util.List;

import static com.TalentWorld.backend.enums.Role.ROLE_ADMIN;
import static com.TalentWorld.backend.enums.Role.ROLE_USER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;

    @Override
    public JobApplicationResponse applyToJob(String jobPostId, User talent, JobApplicationCreateRequest request) {
        log.info("İş başvurusu isteği: jobPostId={}, userId={}", jobPostId, talent.getId());

        if (!talent.getRoles().contains(ROLE_USER)) {
            log.warn("Yetkisiz başvuru girişimi: userId={}, roles={}", talent.getId(), talent.getRoles());
            throw new BusinessException("Only talent users can apply", "ROLE_NOT_ALLOWED", HttpStatus.FORBIDDEN);
        }

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> {
                    log.warn("İş ilanı bulunamadı: jobPostId={}", jobPostId);
                    return new BusinessException("Job post not found", "JOB_POST_NOT_FOUND", HttpStatus.NOT_FOUND);
                });

        if (!jobPost.getIsActive()) {
            log.warn("Pasif ilana başvuru girişimi: jobPostId={}, userId={}", jobPostId, talent.getId());
            throw new BusinessException("Job post is not active", "JOB_POST_INACTIVE", HttpStatus.BAD_REQUEST);
        }

        if (jobApplicationRepository.existsByJobPostAndTalent(jobPost, talent)) {
            log.warn("Mükerrer başvuru girişimi: jobPostId={}, userId={}", jobPostId, talent.getId());
            throw new BusinessException("You already applied to this job", "ALREADY_APPLIED", HttpStatus.CONFLICT);
        }

        JobApplication application = new JobApplication();
        application.setJobPost(jobPost);
        application.setTalent(talent);
        application.setStatus(ApplicationStatus.APPLIED);
        application.setCoverLetter(request.coverLetter());
        application.setAppliedAt(LocalDateTime.now());

        JobApplication saved = jobApplicationRepository.save(application);
        log.info("Başvuru oluşturuldu: applicationId={}, jobPostId={}, userId={}",
                saved.getId(), jobPostId, talent.getId());

        return JobApplicationResponse.toDto(saved);
    }

    @Override
    public List<JobApplicationResponse> getApplicationsForJob(String jobPostId, User recruiter) {
        log.info("İlana ait başvurular getiriliyor: jobPostId={}, userId={}", jobPostId, recruiter.getId());

        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> {
                    log.warn("İş ilanı bulunamadı: jobPostId={}", jobPostId);
                    return new BusinessException("Job post not found", "JOB_POST_NOT_FOUND", HttpStatus.NOT_FOUND);
                });

        boolean isOwner = jobPost.getUser().getId().equals(recruiter.getId());
        boolean isAdmin = recruiter.getRoles().contains(ROLE_ADMIN);

        if (!isOwner && !isAdmin) {
            log.warn("Yetkisiz başvuru listeleme girişimi: jobPostId={}, userId={}", jobPostId, recruiter.getId());
            throw new BusinessException("You are not allowed to view applications", "ACCESS_DENIED", HttpStatus.FORBIDDEN);
        }

        List<JobApplicationResponse> applications = jobApplicationRepository.findByJobPost(jobPost)
                .stream()
                .map(JobApplicationResponse::toDto)
                .toList();

        log.info("Başvurular getirildi: jobPostId={}, toplam={}", jobPostId, applications.size());
        return applications;
    }

    @Override
    public List<MyApplicationResponse> getMyApplications(User talent) {
        log.info("Kullanıcı başvuruları getiriliyor: userId={}", talent.getId());

        if (!talent.getRoles().contains(ROLE_USER)) {
            log.warn("Yetkisiz başvuru görüntüleme girişimi: userId={}", talent.getId());
            throw new BusinessException("Only talent users can view their applications",
                    "ROLE_NOT_ALLOWED", HttpStatus.FORBIDDEN);
        }

        List<MyApplicationResponse> applications = jobApplicationRepository.findByTalent(talent)
                .stream()
                .map(MyApplicationResponse::toDto)
                .toList();

        log.info("Kullanıcı başvuruları getirildi: userId={}, toplam={}", talent.getId(), applications.size());
        return applications;
    }

    @Override
    public JobApplicationResponse updateApplicationStatus(String applicationId, User recruiter,
                                                          JobApplicationStatusUpdateRequest request) {
        log.info("Başvuru durumu güncelleme isteği: applicationId={}, userId={}", applicationId, recruiter.getId());

        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> {
                    log.warn("Başvuru bulunamadı: applicationId={}", applicationId);
                    return new BusinessException("Application not found", "APPLICATION_NOT_FOUND", HttpStatus.NOT_FOUND);
                });

        JobPost jobPost = application.getJobPost();
        boolean isOwner = jobPost.getUser().getId().equals(recruiter.getId());
        boolean isAdmin = recruiter.getRoles().contains(ROLE_ADMIN);

        if (!isOwner && !isAdmin) {
            log.warn("Yetkisiz durum güncelleme girişimi: applicationId={}, userId={}",
                    applicationId, recruiter.getId());
            throw new BusinessException("You are not allowed to update this application",
                    "ACCESS_DENIED", HttpStatus.FORBIDDEN);
        }

        application.setStatus(request.status());
        log.info("Başvuru durumu güncellendi: applicationId={}, yeniDurum={}", applicationId, request.status());

        return JobApplicationResponse.toDto(application);
    }

    @Override
    public PaginationResponse<JobApplicationResponse> findJobApplicationsWithSort(String field) {
        List<JobApplication> applications = jobApplicationRepository.findAll(Sort.by(Sort.Direction.DESC, field));
        List<JobApplicationResponse> response = applications.stream().map(JobApplicationResponse::toDto).toList();
        log.debug("Sıralı başvurular getirildi: field={}, toplam={}", field, response.size());
        return new PaginationResponse<>(applications.size(), response);
    }

    @Override
    public PaginationResponse<JobApplicationResponse> findJobApplicationsWithPage(int page, int size) {
        Page<JobApplication> applications = jobApplicationRepository.findAll(PageRequest.of(page, size));
        List<JobApplicationResponse> response = applications.stream().map(JobApplicationResponse::toDto).toList();
        log.debug("Sayfalı başvurular getirildi: page={}, size={}, toplam={}", page, size, response.size());
        return new PaginationResponse<>(response.size(), response);
    }

    @Override
    public PaginationResponse<JobApplicationResponse> findApplicationsWithPageAndSort(String field, int page, int size) {
        Page<JobApplication> applications = jobApplicationRepository
                .findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, field)));
        List<JobApplicationResponse> response = applications.stream().map(JobApplicationResponse::toDto).toList();
        log.debug("Sayfalı ve sıralı başvurular getirildi: field={}, page={}, size={}, toplam={}",
                field, page, size, response.size());
        return new PaginationResponse<>(response.size(), response);
    }
}