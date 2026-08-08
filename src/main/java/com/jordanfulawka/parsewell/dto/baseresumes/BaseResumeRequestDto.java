package com.jordanfulawka.parsewell.dto.baseresumes;


import java.util.UUID;

public class BaseResumeRequestDto {

    private UUID userId;
    private String content;
    private String originalFileURL;

    public BaseResumeRequestDto() {}

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
                ", content='" + content + '\'' +
                ", originalFileURL='" + originalFileURL + '\'' +
                '}';
    }
}
