package com.jordanfulawka.parsewell.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="generated_cover_letters")
public class GeneratedCoverLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="application_id", unique = true)
    private Application application;

    @Column(name="content")
    private String content;

    @CreationTimestamp
    @Column(name="generated_at")
    private LocalDateTime generatedAt;

    public GeneratedCoverLetter() {}

    public GeneratedCoverLetter(Application application, String content) {
        this.application = application;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    @Override
    public String toString() {
        return "GeneratedCoverLetter{" +
                "id=" + id +
                ", application=" + application +
                ", content='" + content + '\'' +
                ", generatedAt=" + generatedAt +
                '}';
    }
}
