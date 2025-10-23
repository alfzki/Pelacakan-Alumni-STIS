package com.stis.alumni.service;

import com.stis.alumni.dto.workhistory.WorkHistoryRequest;
import com.stis.alumni.dto.workhistory.WorkHistoryResponse;
import com.stis.alumni.dto.workhistory.WorkHistoryUpdateRequest;
import com.stis.alumni.entity.Institution;
import com.stis.alumni.entity.User;
import com.stis.alumni.entity.WorkHistory;
import com.stis.alumni.enums.UserRole;
import com.stis.alumni.exception.BadRequestException;
import com.stis.alumni.exception.ForbiddenException;
import com.stis.alumni.exception.ResourceNotFoundException;
import com.stis.alumni.exception.UnauthorizedException;
import com.stis.alumni.mapper.WorkHistoryMapper;
import com.stis.alumni.repository.InstitutionRepository;
import com.stis.alumni.repository.UserRepository;
import com.stis.alumni.repository.WorkHistoryRepository;
import com.stis.alumni.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkHistoryService {

    private final WorkHistoryRepository workHistoryRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;

    public WorkHistoryService(WorkHistoryRepository workHistoryRepository,
                              InstitutionRepository institutionRepository,
                              UserRepository userRepository) {
        this.workHistoryRepository = workHistoryRepository;
        this.institutionRepository = institutionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkHistoryResponse> getCurrentUserWorkHistories() {
        Long userId = getCurrentUserId();
        return WorkHistoryMapper.toResponses(workHistoryRepository.findByUserIdOrderByStartDateDesc(userId));
    }

    @Transactional(readOnly = true)
    public WorkHistoryResponse getWorkHistory(Long id) {
        WorkHistory workHistory = getWorkHistoryOrThrow(id);
        ensureAccess(workHistory);
        return WorkHistoryMapper.toResponse(workHistory);
    }

    @Transactional
    public WorkHistoryResponse createWorkHistory(WorkHistoryRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        Institution institution = institutionRepository.findById(request.getInstitutionId())
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + request.getInstitutionId()));
        WorkHistory workHistory = WorkHistoryMapper.toEntity(request, user, institution);
        if (request.isCurrentJob()) {
            clearCurrentJobFlag(user.getId());
        }
        WorkHistory saved = workHistoryRepository.save(workHistory);
        return WorkHistoryMapper.toResponse(saved);
    }

    @Transactional
    public WorkHistoryResponse updateWorkHistory(Long id, WorkHistoryUpdateRequest request) {
        WorkHistory workHistory = getWorkHistoryOrThrow(id);
        ensureAccess(workHistory);
        if (request.getStartDate() != null || request.getEndDate() != null) {
            validateDates(request.getStartDate() != null ? request.getStartDate() : workHistory.getStartDate(),
                    request.getEndDate() != null ? request.getEndDate() : workHistory.getEndDate());
        }
        Institution institution = null;
        if (request.getInstitutionId() != null) {
            institution = institutionRepository.findById(request.getInstitutionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + request.getInstitutionId()));
        }
        boolean becomesCurrent = Boolean.TRUE.equals(request.getCurrentJob());
        if (becomesCurrent) {
            clearCurrentJobFlag(workHistory.getUser().getId());
        }
        WorkHistoryMapper.applyUpdate(workHistory, request, institution);
        WorkHistory saved = workHistoryRepository.save(workHistory);
        return WorkHistoryMapper.toResponse(saved);
    }

    @Transactional
    public void deleteWorkHistory(Long id) {
        WorkHistory workHistory = getWorkHistoryOrThrow(id);
        ensureAccess(workHistory);
        workHistoryRepository.delete(workHistory);
    }

    private void validateDates(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate == null) {
            throw new BadRequestException("Start date is required");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException("End date cannot be before start date");
        }
    }

    private void ensureAccess(WorkHistory workHistory) {
        Long currentUserId = getCurrentUserId();
        if (!workHistory.getUser().getId().equals(currentUserId) && !SecurityUtils.hasRole(UserRole.ADMIN)) {
            throw new ForbiddenException("You do not have permission to access this resource");
        }
    }

    private void clearCurrentJobFlag(Long userId) {
        List<WorkHistory> histories = workHistoryRepository.findByUserIdOrderByStartDateDesc(userId);
        List<WorkHistory> updated = histories.stream()
                .filter(WorkHistory::isCurrentJob)
                .peek(history -> history.setCurrentJob(false))
                .toList();
        if (!updated.isEmpty()) {
            workHistoryRepository.saveAll(updated);
        }
    }

    private WorkHistory getWorkHistoryOrThrow(Long id) {
        return workHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Work history not found with id: " + id));
    }

    private Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));
    }
}
