package com.jordanfulawka.parsewell.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="final_materials")
public class FinalMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="application_id")
    private Application application;

    @Column(name="resume_url")
    private String resumeURL;

    @Column(name="cover_letter_url")
    private String coverLetterURL;

    @CreationTimestamp
    @Column(name="uploaded_at")
    private LocalDateTime uploadedAt;

    public FinalMaterial() {}

    public FinalMaterial(Application application, String resumeURL, String coverLetterURL) {
        this.application = application;
        this.resumeURL = resumeURL;
        this.coverLetterURL = coverLetterURL;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getResumeURL() {
        return resumeURL;
    }

    public void setResumeURL(String resumeURL) {
        this.resumeURL = resumeURL;
    }

    public String getCoverLetterURL() {
        return coverLetterURL;
    }

    public void setCoverLetterURL(String coverLetterURL) {
        this.coverLetterURL = coverLetterURL;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @Override
    public String toString() {
        return "FinalMaterial{" +
                "id=" + id +
                ", application=" + application +
                ", resumeURL='" + resumeURL + '\'' +
                ", coverLetterURL='" + coverLetterURL + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}
