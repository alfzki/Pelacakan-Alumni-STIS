package com.stis.alumni.dto.statistics;

import java.util.Map;

public class StatisticsOverviewResponse {

    private long totalAlumni;
    private long totalInstitutions;
    private Map<String, Long> byInstitutionType;
    private Map<String, Long> byAngkatan;
    private Map<String, Long> byEmploymentType;

    public StatisticsOverviewResponse() {
    }

    public long getTotalAlumni() {
        return totalAlumni;
    }

    public void setTotalAlumni(long totalAlumni) {
        this.totalAlumni = totalAlumni;
    }

    public long getTotalInstitutions() {
        return totalInstitutions;
    }

    public void setTotalInstitutions(long totalInstitutions) {
        this.totalInstitutions = totalInstitutions;
    }

    public Map<String, Long> getByInstitutionType() {
        return byInstitutionType;
    }

    public void setByInstitutionType(Map<String, Long> byInstitutionType) {
        this.byInstitutionType = byInstitutionType;
    }

    public Map<String, Long> getByAngkatan() {
        return byAngkatan;
    }

    public void setByAngkatan(Map<String, Long> byAngkatan) {
        this.byAngkatan = byAngkatan;
    }

    public Map<String, Long> getByEmploymentType() {
        return byEmploymentType;
    }

    public void setByEmploymentType(Map<String, Long> byEmploymentType) {
        this.byEmploymentType = byEmploymentType;
    }
}
