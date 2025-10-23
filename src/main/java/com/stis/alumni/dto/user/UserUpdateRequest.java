package com.stis.alumni.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @Size(max = 150)
    private String fullName;

    @Pattern(regexp = "(?i)^(D1|D3|D4)$", message = "programStudi must be one of: D1, D3, D4")
    private String programStudi;

    private Integer tahunLulus;

    @Size(max = 30)
    private String phoneNumber;

    @Size(max = 255)
    private String alamat;

    public UserUpdateRequest() {
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
