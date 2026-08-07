package com.jordanfulawka.parsewell.dto.jobpostings;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class JobPostingResponse {

    @JsonPropertyDescription("The hiring company's name, e.g. 'Stripe'. Not the ATS platform name (e.g. not 'Greenhouse').")
    private String companyName;

    @JsonPropertyDescription("The job title as posted, e.g. 'Junior Backend Engineer'. Omit level suffixes not in the original title.")
    private String roleTitle;

    @JsonPropertyDescription("The job's location as posted, e.g. 'Toronto, ON' or 'Remote'. If multiple locations are listed, use the primary/first one. Return an empty string if not stated.")
    private String location;

    @JsonPropertyDescription("The exact job URL as given in the 'URL:' line of the input. Copy verbatim, do not modify or normalize")
    private String jobUrl;

    @JsonPropertyDescription("The full job description text, cleaned of navigation/apply-button boilerplate.")
    private String jobDescription;

    public JobPostingResponse () {}

    public JobPostingResponse(String companyName, String roleTitle, String location, String jobUrl, String jobDescription) {
        this.companyName = companyName;
        this.roleTitle = roleTitle;
        this.location = location;
        this.jobUrl = jobUrl;
        this.jobDescription = jobDescription;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public void setJobUrl(String jobUrl) {
        this.jobUrl = jobUrl;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    @Override
    public String toString() {
        return "JobPostingResponse{" +
                "companyName='" + companyName + '\'' +
                ", roleTitle='" + roleTitle + '\'' +
                ", location='" + location + '\'' +
                ", jobUrl='" + jobUrl + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                '}';
    }
}
