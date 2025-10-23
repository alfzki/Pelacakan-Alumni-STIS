package com.stis.alumni.dto.dashboard;

public class DashboardSummaryResponse {

    private long totalAlumni;
    private long activeAlumni;
    private long inactiveAlumni;
    private long workingAlumni;
    private long nonWorkingAlumni;
    private long totalInstitutions;

    public DashboardSummaryResponse() {
    }

    public DashboardSummaryResponse(long totalAlumni, long activeAlumni, long inactiveAlumni, long workingAlumni, long nonWorkingAlumni, long totalInstitutions) {
        this.totalAlumni = totalAlumni;
        this.activeAlumni = activeAlumni;
        this.inactiveAlumni = inactiveAlumni;
        this.workingAlumni = workingAlumni;
        this.nonWorkingAlumni = nonWorkingAlumni;
        this.totalInstitutions = totalInstitutions;
    }

    public long getTotalAlumni() {
        return totalAlumni;
    }

    public void setTotalAlumni(long totalAlumni) {
        this.totalAlumni = totalAlumni;
    }

    public long getActiveAlumni() {
        return activeAlumni;
    }

    public void setActiveAlumni(long activeAlumni) {
        this.activeAlumni = activeAlumni;
    }

    public long getInactiveAlumni() {
        return inactiveAlumni;
    }

    public void setInactiveAlumni(long inactiveAlumni) {
        this.inactiveAlumni = inactiveAlumni;
    }

    public long getWorkingAlumni() {
        return workingAlumni;
    }

    public void setWorkingAlumni(long workingAlumni) {
        this.workingAlumni = workingAlumni;
    }

    public long getNonWorkingAlumni() {
        return nonWorkingAlumni;
    }

    public void setNonWorkingAlumni(long nonWorkingAlumni) {
        this.nonWorkingAlumni = nonWorkingAlumni;
    }

    public long getTotalInstitutions() {
        return totalInstitutions;
    }

    public void setTotalInstitutions(long totalInstitutions) {
        this.totalInstitutions = totalInstitutions;
    }
}
