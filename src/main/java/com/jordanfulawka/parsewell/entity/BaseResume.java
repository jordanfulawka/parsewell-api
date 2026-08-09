package com.jordanfulawka.parsewell.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="base_resumes")
public class BaseResume {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", unique = true ,nullable = false)
    private User user;

    @Column(name="content", columnDefinition = "text")
    private String content;

    @Column(name="file_name", columnDefinition = "text")
    private String fileName;

    @Column(name="original_file_url", columnDefinition = "text")
    private String originalFileURL;

    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BaseResume() {}

    public BaseResume(User user, String content, String fileName, String originalFileURL) {
        this.user = user;
        this.content = content;
        this.fileName = fileName;
        this.originalFileURL = originalFileURL;
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


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileURL() {
        return originalFileURL;
    }

    public void setOriginalFileURL(String originalFileURL) {
        this.originalFileURL = originalFileURL;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
