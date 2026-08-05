package com.jordanfulawka.parsewell.dto.applications;

import com.jordanfulawka.parsewell.entity.enums.ApplicationChannel;
import com.jordanfulawka.parsewell.entity.enums.ApplicationStatus;

import java.util.UUID;

public class ApplicationRequestDto {

    private UUID userId;
    private UUID baseResumeId;
    private String companyName;
    private String roleTitle;
    private String jobURL;
    private String jobDescription;
    private ApplicationChannel applicationChannel;
    private ApplicationStatus applicationStatus;
    private String notes;

    public ApplicationRequestDto() {}

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getBaseResumeId() {
        return baseResumeId;
    }

    public void setBaseResumeId(UUID baseResumeId) {
        this.baseResumeId = baseResumeId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public String getJobURL() {
        return jobURL;
    }

    public void setJobURL(String jobURL) {
        this.jobURL = jobURL;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public ApplicationChannel getApplicationChannel() {
        return applicationChannel;
    }

    public void setApplicationChannel(ApplicationChannel applicationChannel) {
        this.applicationChannel = applicationChannel;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
