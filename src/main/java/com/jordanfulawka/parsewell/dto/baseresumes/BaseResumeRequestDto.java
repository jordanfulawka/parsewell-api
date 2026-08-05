package com.jordanfulawka.parsewell.dto.baseresumes;


import java.util.UUID;

public class BaseResumeRequestDto {

    private UUID userId;
    private String label;
    private String content;
    private String originalFileURL;

    public BaseResumeRequestDto() {}

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "BaseResumeRequestDto{" +
                "userId=" + userId +
                ", label='" + label + '\'' +
                ", content='" + content + '\'' +
                ", originalFileURL='" + originalFileURL + '\'' +
                '}';
    }
}
