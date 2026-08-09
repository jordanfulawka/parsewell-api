package com.jordanfulawka.parsewell.dto.baseresumes;

import java.time.LocalDateTime;
import java.util.UUID;

public record BaseResumeResponseDto(
        UUID id, UUID userId, String content, String fileName, String originalFileURL, LocalDateTime createdAt
) {
}
