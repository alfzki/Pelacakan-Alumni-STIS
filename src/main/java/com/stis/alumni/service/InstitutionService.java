package com.stis.alumni.service;

import com.stis.alumni.dto.OptionResponse;
import com.stis.alumni.dto.PageResponse;
import com.stis.alumni.dto.institution.InstitutionDetailResponse;
import com.stis.alumni.dto.institution.InstitutionEmployeeResponse;
import com.stis.alumni.dto.institution.InstitutionListItemResponse;
import com.stis.alumni.dto.institution.InstitutionRequest;
import com.stis.alumni.dto.institution.InstitutionResponse;
import com.stis.alumni.dto.institution.InstitutionSearchCriteria;
import com.stis.alumni.dto.institution.InstitutionUpdateRequest;
import com.stis.alumni.entity.Institution;
import com.stis.alumni.exception.BadRequestException;
import com.stis.alumni.exception.ResourceNotFoundException;
import com.stis.alumni.mapper.InstitutionMapper;
import com.stis.alumni.mapper.WorkHistoryMapper;
import com.stis.alumni.repository.InstitutionRepository;
import com.stis.alumni.repository.WorkHistoryRepository;
import com.stis.alumni.specification.InstitutionSpecifications;
import com.stis.alumni.util.PageUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstitutionService {

    private final InstitutionRepository institutionRepository;
    private final WorkHistoryRepository workHistoryRepository;

    public InstitutionService(InstitutionRepository institutionRepository, WorkHistoryRepository workHistoryRepository) {
        this.institutionRepository = institutionRepository;
        this.workHistoryRepository = workHistoryRepository;
    }

    @Transactional
    public InstitutionResponse createInstitution(InstitutionRequest request) {
        if (institutionRepository.existsByNameIgnoreCase(request.getName())) {
            throw new BadRequestException("Institution name already exists");
        }
        Institution institution = InstitutionMapper.toEntity(request);
        Institution saved = institutionRepository.save(institution);
        return InstitutionMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<InstitutionListItemResponse> getInstitutions(int page, int size, String sortBy, String sortDirection, InstitutionSearchCriteria criteria) {
        String sortProperty = StringUtils.hasText(sortBy) ? sortBy : "name";
        Sort.Direction direction = Sort.Direction.ASC;
        if (StringUtils.hasText(sortDirection)) {
            try {
                direction = Sort.Direction.fromString(sortDirection);
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Invalid sort direction: " + sortDirection);
            }
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        Page<Institution> result = institutionRepository.findAll(InstitutionSpecifications.byCriteria(criteria), pageable);
        Page<InstitutionListItemResponse> mapped = result.map(entity -> {
            long count = workHistoryRepository.countDistinctUserByInstitution(entity.getId());
            return InstitutionMapper.toListItem(entity, count);
        });
        return PageUtils.from(mapped);
    }

    @Transactional(readOnly = true)
    public InstitutionDetailResponse getInstitutionDetail(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + id));
        long alumniCount = workHistoryRepository.countDistinctUserByInstitution(id);
        List<InstitutionEmployeeResponse> employees = WorkHistoryMapper.toEmployeeResponses(
                workHistoryRepository.findByInstitutionIdAndCurrentJobTrueOrderByStartDateDesc(id)
        );
        return InstitutionMapper.toDetail(institution, alumniCount, employees);
    }

    @Transactional
    public InstitutionResponse updateInstitution(Long id, InstitutionUpdateRequest request) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + id));
        InstitutionMapper.applyUpdate(institution, request);
        Institution saved = institutionRepository.save(institution);
        return InstitutionMapper.toResponse(saved);
    }

    @Transactional
    public void deleteInstitution(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institution not found with id: " + id));
        long relatedWorkHistories = workHistoryRepository.countDistinctUserByInstitution(id);
        if (relatedWorkHistories > 0) {
            throw new BadRequestException("Cannot delete institution with existing work histories");
        }
        institutionRepository.delete(institution);
    }

    @Transactional(readOnly = true)
    public List<OptionResponse> getInstitutionOptions(String search) {
        List<Institution> institutions;
        if (StringUtils.hasText(search)) {
            InstitutionSearchCriteria criteria = new InstitutionSearchCriteria();
            criteria.setSearch(search);
            institutions = institutionRepository.findAll(InstitutionSpecifications.byCriteria(criteria), PageRequest.of(0, 10)).getContent();
        } else {
            institutions = institutionRepository.findAll(PageRequest.of(0, 10, Sort.by("name").ascending())).getContent();
        }
        return institutions.stream()
                .map(inst -> new OptionResponse(inst.getName(), String.valueOf(inst.getId())))
                .collect(Collectors.toList());
    }
}
