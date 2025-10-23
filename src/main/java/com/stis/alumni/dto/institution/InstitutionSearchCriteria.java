package com.stis.alumni.dto.institution;

import com.stis.alumni.enums.InstitutionType;

public class InstitutionSearchCriteria {

    private String search;
    private InstitutionType type;
    private String province;
    private String city;

    public InstitutionSearchCriteria() {
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
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
}
