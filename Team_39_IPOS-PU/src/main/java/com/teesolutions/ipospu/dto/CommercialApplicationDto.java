package com.teesolutions.ipospu.dto;

public class CommercialApplicationDto {
    private final String companyRegistrationNumber;
    private final String directorName;
    private final String businessType;
    private final String address;
    private final String email;

    public CommercialApplicationDto(
            String companyRegistrationNumber,
            String directorName,
            String businessType,
            String address,
            String email
    ) {
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.directorName = directorName;
        this.businessType = businessType;
        this.address = address;
        this.email = email;
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public String getDirectorName() {
        return directorName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }
}
