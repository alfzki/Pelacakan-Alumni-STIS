package com.stis.alumni.mapper;

import com.stis.alumni.dto.institution.InstitutionDetailResponse;
import com.stis.alumni.dto.institution.InstitutionEmployeeResponse;
import com.stis.alumni.dto.institution.InstitutionListItemResponse;
import com.stis.alumni.dto.institution.InstitutionRequest;
import com.stis.alumni.dto.institution.InstitutionResponse;
import com.stis.alumni.dto.institution.InstitutionSimpleResponse;
import com.stis.alumni.dto.institution.InstitutionUpdateRequest;
import com.stis.alumni.entity.Institution;

import java.util.List;

public final class InstitutionMapper {

    private InstitutionMapper() {
    }

    public static Institution toEntity(InstitutionRequest request) {
        Institution institution = new Institution();
        applyRequest(institution, request);
        return institution;
    }

    public static void applyRequest(Institution entity, InstitutionRequest request) {
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setProvince(request.getProvince());
        entity.setCity(request.getCity());
        entity.setAddress(request.getAddress());
        entity.setDescription(request.getDescription());
    }

    public static InstitutionResponse toResponse(Institution entity) {
        if (entity == null) {
            return null;
        }
        InstitutionResponse response = new InstitutionResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setType(entity.getType());
        response.setProvince(entity.getProvince());
        response.setCity(entity.getCity());
        response.setAddress(entity.getAddress());
        response.setDescription(entity.getDescription());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }

    public static InstitutionSimpleResponse toSimple(Institution entity) {
        if (entity == null) {
            return null;
        }
        InstitutionSimpleResponse response = new InstitutionSimpleResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setType(entity.getType());
        response.setProvince(entity.getProvince());
        response.setCity(entity.getCity());
        return response;
    }

    public static InstitutionListItemResponse toListItem(Institution entity, long workingAlumniCount) {
        if (entity == null) {
            return null;
        }
        InstitutionListItemResponse response = new InstitutionListItemResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setType(entity.getType());
        response.setProvince(entity.getProvince());
        response.setCity(entity.getCity());
        response.setWorkingAlumniCount(workingAlumniCount);
        return response;
    }

    public static void applyUpdate(Institution entity, InstitutionUpdateRequest request) {
        if (request == null) {
            return;
        }
        if (request.getName() != null) {
            entity.setName(request.getName());
        }
        if (request.getType() != null) {
            entity.setType(request.getType());
        }
        if (request.getProvince() != null) {
            entity.setProvince(request.getProvince());
        }
        if (request.getCity() != null) {
            entity.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            entity.setAddress(request.getAddress());
        }
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }
    }

    public static InstitutionDetailResponse toDetail(Institution entity, long alumniCount, List<InstitutionEmployeeResponse> employees) {
        if (entity == null) {
            return null;
        }
        InstitutionDetailResponse response = new InstitutionDetailResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setType(entity.getType());
        response.setProvince(entity.getProvince());
        response.setCity(entity.getCity());
        response.setAddress(entity.getAddress());
        response.setDescription(entity.getDescription());
        response.setAlumniCount(alumniCount);
        response.setCurrentEmployees(employees);
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
