package com.TalentWorld.backend.service.impl;

import com.TalentWorld.backend.dto.request.JobPostCreateRequest;
import com.TalentWorld.backend.dto.request.JobPostUpdateRequest;
import com.TalentWorld.backend.dto.response.JobPostResponse;
import com.TalentWorld.backend.dto.response.PaginationResponse;
import com.TalentWorld.backend.entity.JobPost;
import com.TalentWorld.backend.entity.User;
import com.TalentWorld.backend.enums.Role;
import com.TalentWorld.backend.excepiton.BusinessException;
import com.TalentWorld.backend.repository.JobPostRepository;
import com.TalentWorld.backend.service.JobPostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.TalentWorld.backend.enums.Role.ROLE_RECRUITER;
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobPostServiceImpl implements JobPostService {

    private final JobPostRepository jobPostRepository;

    @Cacheable(value = "jobPosts", key="#root.methodName")
    @Override
    public List<JobPostResponse> getJobPosts() {
        List<JobPost> jobPosts = jobPostRepository.findAll();
        log.info("İş ilanları getirildi: toplam={}", jobPosts.size());
        return jobPosts.stream().map(JobPostResponse::toDto).collect(Collectors.toList());
    }


    @Cacheable(value = "jobPost", key="#id")
    @Override
    public JobPostResponse getJobPostById(String id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> {
            log.warn("İş ilanı bulunamadı: id={}", id);
            return new BusinessException("Job post not found with id: " + id,
                    "JOB_POST_NOT_FOUND",
                    HttpStatus.NOT_FOUND
            );
        });
        log.info("İş ilanı getirildi: id={}", id);
        return JobPostResponse.toDto(jobPost);
    }

    @Override
    public JobPostResponse createJobPost(User recurringUser, JobPostCreateRequest request) {
        log.info("İş ilanı oluşturma isteği: userId={}", recurringUser.getId());

        if (!recurringUser.getRoles().contains(ROLE_RECRUITER)) {
            log.warn("Yetkisiz ilan oluşturma girişimi: userId={}, roles={}",
                    recurringUser.getId(), recurringUser.getRoles());
            throw new BusinessException("User Not RECRUITER", "USER_ROLE_NOT_ALLOWED", HttpStatus.FORBIDDEN);
        }

        if (request.maxExperienceYear() < request.minExperienceYear()) {
            log.warn("Geçersiz deneyim yılı aralığı: min={}, max={}",
                    request.minExperienceYear(), request.maxExperienceYear());
            throw new BusinessException("Min experience year can not be bigger than max experience year",
                    "MAX_EXPERIENCE_YEAR", HttpStatus.BAD_REQUEST);
        }

        JobPost jobPost = JobPostCreateRequest.toEntityDto(request);
        jobPost.setUser(recurringUser);
        JobPost saved = jobPostRepository.save(jobPost);

        log.info("İş ilanı oluşturuldu: id={}, userId={}", saved.getId(), recurringUser.getId());
        return JobPostResponse.toDto(saved);
    }

    @Override
    @Transactional
    public JobPostResponse updateJobPost(User currentUser, JobPostUpdateRequest request, String id) {
        log.info("İş ilanı güncelleme isteği: id={}, userId={}", id, currentUser.getId());

        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> {
            log.warn("Güncellenecek ilan bulunamadı: id={}", id);
            return new BusinessException("Job post not found with id: " + id,
                    "JOB_POST_NOT_FOUND", HttpStatus.NOT_FOUND);
        });

        boolean isOwner = jobPost.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().contains(Role.ROLE_ADMIN);

        if (!isOwner && !isAdmin) {
            log.warn("Yetkisiz güncelleme girişimi: id={}, userId={}", id, currentUser.getId());
            throw new BusinessException("You are not allowed to update this job post",
                    "ACCESS_DENIED", HttpStatus.FORBIDDEN);
        }

        request.applyTo(jobPost);
        JobPost updated = jobPostRepository.save(jobPost);

        log.info("İş ilanı güncellendi: id={}, userId={}", id, currentUser.getId());
        return JobPostResponse.toDto(updated);
    }

    @Override
    public String deleteJobPostById(String id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> {
            log.warn("Silinecek ilan bulunamadı: id={}", id);
            return new BusinessException("Job post not found with id: " + id,
                    "JOB_POST_NOT_FOUND", HttpStatus.NOT_FOUND);
        });

        jobPostRepository.delete(jobPost);
        log.info("İş ilanı silindi: id={}", id);
        return "Post deleted successfully with id: " + id;
    }

    @Override
    public PaginationResponse<JobPostResponse> findJobsWithSort(String field) {
        List<JobPost> jobPosts = jobPostRepository.findAll(Sort.by(Sort.Direction.ASC, field));
        List<JobPostResponse> response = jobPosts.stream().map(JobPostResponse::toDto).collect(Collectors.toList());
        log.debug("Sıralı ilanlar getirildi: field={}, toplam={}", field, response.size());
        return new PaginationResponse<>(response.size(), response);
    }

    @Override
    public PaginationResponse<JobPostResponse> findJobsWithPage(int page, int size) {
        Page<JobPost> pageJobs = jobPostRepository.findAll(PageRequest.of(page, size));
        List<JobPostResponse> response = pageJobs.stream().map(JobPostResponse::toDto).collect(Collectors.toList());
        log.debug("Sayfalı ilanlar getirildi: page={}, size={}, toplam={}", page, size, response.size());
        return new PaginationResponse<>(response.size(), response);
    }

    @Override
    public PaginationResponse<JobPostResponse> findJobsWithPageAndSort(String field, int page, int size) {
        Page<JobPost> pageJobs = jobPostRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, field)));
        List<JobPostResponse> response = pageJobs.stream().map(JobPostResponse::toDto).collect(Collectors.toList());
        log.debug("Sayfalı ve sıralı ilanlar getirildi: field={}, page={}, size={}, toplam={}", field, page, size, response.size());
        return new PaginationResponse<>(response.size(), response);
    }
}
