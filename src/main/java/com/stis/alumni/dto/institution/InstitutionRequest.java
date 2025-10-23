package com.stis.alumni.dto.institution;

import com.stis.alumni.enums.InstitutionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InstitutionRequest {

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotNull
    private InstitutionType type;

    @Size(max = 100)
    private String province;

    @Size(max = 100)
    private String city;

    @Size(max = 255)
    private String address;

    @Size(max = 500)
    private String description;

    public InstitutionRequest() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
