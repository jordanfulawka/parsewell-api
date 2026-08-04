package com.jordanfulawka.parsewell.entity;

import com.jordanfulawka.parsewell.entity.enums.ApplicationChannel;
import com.jordanfulawka.parsewell.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="base_resume_id")
    private BaseResume baseResume;

    @Column(name="company_name")
    private String companyName;

    @Column(name="role_title")
    private String roleTitle;

    @Column(name="job_url")
    private String jobURL;

    @Column(name="job_description")
    private String jobDescription;

    @Enumerated(EnumType.STRING)
    @Column(name="application_channel")
    private ApplicationChannel applicationChannel;

    @Enumerated(EnumType.STRING)
    @Column(name="application_status")
    private ApplicationStatus applicationStatus;

    @Column(name="notes")
    private String notes;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    public Application() {}

    public Application(User user, BaseResume baseResume, String companyName, String roleTitle, String jobURL, String jobDescription, ApplicationChannel applicationChannel, ApplicationStatus applicationStatus, String notes) {
        this.user = user;
        this.baseResume = baseResume;
        this.companyName = companyName;
        this.roleTitle = roleTitle;
        this.jobURL = jobURL;
        this.jobDescription = jobDescription;
        this.applicationChannel = applicationChannel;
        this.applicationStatus = applicationStatus;
        this.notes = notes;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BaseResume getBaseResume() {
        return baseResume;
    }

    public void setBaseResume(BaseResume baseResume) {
        this.baseResume = baseResume;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", user=" + user +
                ", baseResume=" + baseResume +
                ", companyName='" + companyName + '\'' +
                ", roleTitle='" + roleTitle + '\'' +
                ", jobURL='" + jobURL + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", applicationChannel=" + applicationChannel +
                ", applicationStatus=" + applicationStatus +
                ", notes='" + notes + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
