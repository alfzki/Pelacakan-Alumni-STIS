package com.stis.alumni.mapper;

import com.stis.alumni.dto.institution.InstitutionEmployeeResponse;
import com.stis.alumni.dto.institution.InstitutionSimpleResponse;
import com.stis.alumni.dto.workhistory.WorkHistoryRequest;
import com.stis.alumni.dto.workhistory.WorkHistoryResponse;
import com.stis.alumni.dto.workhistory.WorkHistoryUpdateRequest;
import com.stis.alumni.entity.Institution;
import com.stis.alumni.entity.User;
import com.stis.alumni.entity.WorkHistory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class WorkHistoryMapper {

    private WorkHistoryMapper() {
    }

    public static WorkHistory toEntity(WorkHistoryRequest request, User user, Institution institution) {
        WorkHistory entity = new WorkHistory();
        entity.setUser(user);
        entity.setInstitution(institution);
        applyRequest(entity, request, institution);
        return entity;
    }

    public static void applyRequest(WorkHistory entity, WorkHistoryRequest request, Institution institution) {
        entity.setInstitution(institution);
        entity.setPosition(request.getPosition());
        entity.setEmploymentType(request.getEmploymentType());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setCurrentJob(request.isCurrentJob());
        entity.setDescription(request.getDescription());
    }

    public static WorkHistoryResponse toResponse(WorkHistory entity) {
        if (entity == null) {
            return null;
        }
        WorkHistoryResponse response = new WorkHistoryResponse();
        response.setId(entity.getId());
        response.setPosition(entity.getPosition());
        response.setEmploymentType(entity.getEmploymentType());
        response.setStartDate(entity.getStartDate());
        response.setEndDate(entity.getEndDate());
        response.setCurrentJob(entity.isCurrentJob());
        response.setDescription(entity.getDescription());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setInstitution(toInstitutionSimple(entity.getInstitution()));
        return response;
    }

    public static List<WorkHistoryResponse> toResponses(List<WorkHistory> entities) {
        return entities == null ? List.of() : entities.stream()
                .filter(Objects::nonNull)
                .map(WorkHistoryMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static InstitutionEmployeeResponse toEmployeeResponse(WorkHistory entity) {
        if (entity == null || entity.getUser() == null) {
            return null;
        }
        InstitutionEmployeeResponse response = new InstitutionEmployeeResponse();
        response.setUserId(entity.getUser().getId());
        response.setFullName(entity.getUser().getFullName());
        response.setPosition(entity.getPosition());
        response.setStartDate(entity.getStartDate());
        return response;
    }

    public static List<InstitutionEmployeeResponse> toEmployeeResponses(List<WorkHistory> entities) {
        return entities == null ? List.of() : entities.stream()
                .filter(Objects::nonNull)
                .map(WorkHistoryMapper::toEmployeeResponse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static void applyUpdate(WorkHistory entity, WorkHistoryUpdateRequest request, Institution institution) {
        if (request == null) {
            return;
        }
        if (institution != null) {
            entity.setInstitution(institution);
        }
        if (request.getPosition() != null) {
            entity.setPosition(request.getPosition());
        }
        if (request.getEmploymentType() != null) {
            entity.setEmploymentType(request.getEmploymentType());
        }
        if (request.getStartDate() != null) {
            entity.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            entity.setEndDate(request.getEndDate());
        }
        if (request.getCurrentJob() != null) {
            entity.setCurrentJob(request.getCurrentJob());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
    }

    private static InstitutionSimpleResponse toInstitutionSimple(Institution institution) {
        if (institution == null) {
            return null;
        }
        InstitutionSimpleResponse response = new InstitutionSimpleResponse();
        response.setId(institution.getId());
        response.setName(institution.getName());
        response.setType(institution.getType());
        response.setProvince(institution.getProvince());
        response.setCity(institution.getCity());
        return response;
    }
}
