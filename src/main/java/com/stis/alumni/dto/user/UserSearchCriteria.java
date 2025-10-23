package com.stis.alumni.dto.user;

import com.stis.alumni.enums.UserRole;
import com.stis.alumni.enums.UserStatus;

public class UserSearchCriteria {

    private String search;
    private Integer angkatan;
    private String programStudi;
    private Integer tahunLulus;
    private UserStatus status;
    private UserRole role;

    public UserSearchCriteria() {
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Integer getAngkatan() {
        return angkatan;
    }

    public void setAngkatan(Integer angkatan) {
        this.angkatan = angkatan;
    }

    public String getProgramStudi() {
        return programStudi;
    }

    public void setProgramStudi(String programStudi) {
        this.programStudi = programStudi;
    }

    public Integer getTahunLulus() {
        return tahunLulus;
    }

    public void setTahunLulus(Integer tahunLulus) {
        this.tahunLulus = tahunLulus;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
