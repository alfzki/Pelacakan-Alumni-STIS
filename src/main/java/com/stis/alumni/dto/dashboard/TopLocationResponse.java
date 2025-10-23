package com.stis.alumni.dto.dashboard;

public class TopLocationResponse {

    private String province;
    private String city;
    private long totalAlumni;

    public TopLocationResponse() {
    }

    public TopLocationResponse(String province, String city, long totalAlumni) {
        this.province = province;
        this.city = city;
        this.totalAlumni = totalAlumni;
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
