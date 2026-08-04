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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    @Column(name="label")
    private String label;

    @Column(name="content")
    private String content;

    @Column(name="original_file_url")
    private String originalFileURL;

    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BaseResume() {}

    public BaseResume(User user, String label, String content, String originalFileURL) {
        this.user = user;
        this.label = label;
        this.content = content;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
