package com.stis.alumni.dto.institution;

import com.stis.alumni.enums.InstitutionType;

public class InstitutionListItemResponse {

    private Long id;
    private String name;
    private InstitutionType type;
    private String province;
    private String city;
    private long workingAlumniCount;

    public InstitutionListItemResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getWorkingAlumniCount() {
        return workingAlumniCount;
    }

    public void setWorkingAlumniCount(long workingAlumniCount) {
        this.workingAlumniCount = workingAlumniCount;
    }
}
