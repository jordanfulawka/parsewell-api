package com.jordanfulawka.parsewell.dto.applications;

import com.jordanfulawka.parsewell.entity.enums.ApplicationChannel;
import com.jordanfulawka.parsewell.entity.enums.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApplicationDTO(UUID id, String companyName, String roleTitle, String location, String jobURL, ApplicationChannel applicationChannel, ApplicationStatus applicationStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
