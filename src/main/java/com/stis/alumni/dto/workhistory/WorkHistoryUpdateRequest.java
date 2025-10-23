package com.stis.alumni.dto.workhistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stis.alumni.enums.EmploymentType;

import java.time.LocalDate;

public class WorkHistoryUpdateRequest {

    private Long institutionId;
    private String position;
    private EmploymentType employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    @JsonProperty("isCurrentJob")
    private Boolean currentJob;
    private String description;

    public WorkHistoryUpdateRequest() {
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
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
    public Boolean getCurrentJob() {
        return currentJob;
    }

    @JsonProperty("isCurrentJob")
    public void setCurrentJob(Boolean currentJob) {
        this.currentJob = currentJob;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
