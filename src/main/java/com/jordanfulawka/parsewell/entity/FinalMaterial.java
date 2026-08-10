package com.jordanfulawka.parsewell.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(name="resume_key")
    private String resumeKey;

    @Column(name="resume_filename")
    private String resumeFilename;

    @Column(name="cover_letter_key")
    private String coverLetterKey;

    @Column(name="cover_letter_filename")
    private String coverLetterFilename;

    @UpdateTimestamp
    @Column(name="last_updated")
    private LocalDateTime uploadedAt;

    public FinalMaterial() {}

    public FinalMaterial(String resumeKey, String resumeFilename, String coverLetterKey, String coverLetterFilename) {
        this.resumeKey = resumeKey;
        this.resumeFilename = resumeFilename;
        this.coverLetterKey = coverLetterKey;
        this.coverLetterFilename = coverLetterFilename;
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

    public String getResumeKey() {
        return resumeKey;
    }

    public void setResumeKey(String resumeKey) {
        this.resumeKey = resumeKey;
    }

    public String getResumeFilename() {
        return resumeFilename;
    }

    public void setResumeFilename(String resumeFilename) {
        this.resumeFilename = resumeFilename;
    }

    public String getCoverLetterKey() {
        return coverLetterKey;
    }

    public void setCoverLetterKey(String coverLetterKey) {
        this.coverLetterKey = coverLetterKey;
    }

    public String getCoverLetterFilename() {
        return coverLetterFilename;
    }

    public void setCoverLetterFilename(String coverLetterFilename) {
        this.coverLetterFilename = coverLetterFilename;
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
                ", resumeKey='" + resumeKey + '\'' +
                ", resumeFilename='" + resumeFilename + '\'' +
                ", coverLetterKey='" + coverLetterKey + '\'' +
                ", coverLetterFilename='" + coverLetterFilename + '\'' +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}
