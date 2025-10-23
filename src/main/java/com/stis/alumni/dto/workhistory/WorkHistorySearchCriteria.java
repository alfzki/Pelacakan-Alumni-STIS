package com.stis.alumni.dto.workhistory;

import com.stis.alumni.enums.EmploymentType;

public class WorkHistorySearchCriteria {

    private Long userId;
    private Long institutionId;
    private EmploymentType employmentType;
    private Boolean currentJob;
    private String province;
    private String city;

    public WorkHistorySearchCriteria() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public Boolean getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Boolean currentJob) {
        this.currentJob = currentJob;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
