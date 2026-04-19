package com.berrybyte.ACC.model;

/**
 * Represents pending application row.
 */
public class PendingApplicationRow {
    private final int id;
    private final String name;
    private final String companyName;
    private final String companyRegistrationNumber;
    private final String phoneNumber;
    private final String email;
    private final String submissionDate;
    private final String address;
/**
 * Creates a new PendingApplicationRow instance.
 * This method coordinates the main operation for this action.
 *
 * @param id id
 * @param name name
 * @param companyName company name
 * @param companyRegistrationNumber company registration number
 * @param phoneNumber phone number
 * @param email email
 * @param submissionDate submission date
 * @param address address
 */

    public PendingApplicationRow(int id,
                                  String name,
                                  String companyName,
                                  String companyRegistrationNumber,
                                  String phoneNumber,
                                  String email,
                                  String submissionDate,
                                  String address) {
        this.id = id;
        this.name = name;
        this.companyName = companyName;
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.submissionDate = submissionDate;
        this.address = address;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCompanyName() { return companyName; }
    public String getCompanyRegistrationNumber() { return companyRegistrationNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getSubmissionDate() { return submissionDate; }
    public String getAddress() { return address; }
}
