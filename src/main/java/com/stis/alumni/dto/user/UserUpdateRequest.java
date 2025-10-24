package com.stis.alumni.dto.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {

    @Size(max = 150)
    private String fullName;

    @Pattern(regexp = "(?i)^(D3|D4)$", message = "programStudi must be one of: D3, D4")
    private String programStudi;

    @Pattern(
            regexp = "(?i)^(D4\\s+komputasi\\s+statistik|D4\\s+statistik|D3\\s+statistik)$",
            message = "jurusan must be one of: D4 komputasi statistik, D4 statistik, D3 statistik"
    )
    private String jurusan;

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

    public String getJurusan() {
        return jurusan;
    }

    public void setJurusan(String jurusan) {
        this.jurusan = jurusan;
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
