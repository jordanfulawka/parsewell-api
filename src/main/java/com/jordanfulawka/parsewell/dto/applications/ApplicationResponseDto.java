package com.jordanfulawka.parsewell.dto.applications;

import com.jordanfulawka.parsewell.entity.enums.ApplicationChannel;
import com.jordanfulawka.parsewell.entity.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationResponseDto(
        UUID id, UUID userId, UUID baseResumeId, String companyName, String roleTitle, String location, String jobURL, String jobDescription,
        ApplicationChannel applicationChannel, ApplicationStatus applicationStatus, String notes, LocalDateTime createdAt, LocalDateTime updatedAt
) {
}
