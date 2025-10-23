package com.stis.alumni.dto.workhistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stis.alumni.dto.institution.InstitutionSimpleResponse;
import com.stis.alumni.enums.EmploymentType;

import java.time.Instant;
import java.time.LocalDate;

public class WorkHistoryResponse {

    private Long id;
    private InstitutionSimpleResponse institution;
    private String position;
    private EmploymentType employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    @JsonProperty("isCurrentJob")
    private boolean currentJob;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    public WorkHistoryResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InstitutionSimpleResponse getInstitution() {
        return institution;
    }

    public void setInstitution(InstitutionSimpleResponse institution) {
        this.institution = institution;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("isCurrentJob")
    public boolean isCurrentJob() {
        return currentJob;
    }

    @JsonProperty("isCurrentJob")
    public void setCurrentJob(boolean currentJob) {
        this.currentJob = currentJob;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
