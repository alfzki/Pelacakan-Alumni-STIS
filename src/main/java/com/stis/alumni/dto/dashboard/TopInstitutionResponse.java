package com.stis.alumni.dto.dashboard;

import com.stis.alumni.enums.InstitutionType;

public class TopInstitutionResponse {

    private Long institutionId;
    private String institutionName;
    private InstitutionType type;
    private String province;
    private String city;
    private long totalAlumni;

    public TopInstitutionResponse() {
    }

    public TopInstitutionResponse(Long institutionId, String institutionName, InstitutionType type, String province, String city, long totalAlumni) {
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.type = type;
        this.province = province;
        this.city = city;
        this.totalAlumni = totalAlumni;
    }

    public Long getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public InstitutionType getType() {
        return type;
    }

    public void setType(InstitutionType type) {
        this.type = type;
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

    public long getTotalAlumni() {
        return totalAlumni;
    }

    public void setTotalAlumni(long totalAlumni) {
        this.totalAlumni = totalAlumni;
    }
}
