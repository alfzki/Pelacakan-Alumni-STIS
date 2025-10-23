package com.stis.alumni.dto.workhistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stis.alumni.enums.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class WorkHistoryRequest {

    @NotNull
    private Long institutionId;

    @NotBlank
    @Size(max = 150)
    private String position;

    @NotNull
    private EmploymentType employmentType;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    @JsonProperty("isCurrentJob")
    private boolean currentJob;

    @Size(max = 500)
    private String description;

    public WorkHistoryRequest() {
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
}
