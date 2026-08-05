package com.jordanfulawka.parsewell.dto.editsuggestions;

import com.jordanfulawka.parsewell.entity.enums.EditType;

import java.util.UUID;

public record EditSuggestionResponse(
        UUID id, UUID applicationId, String section, String beforeText, String afterText, String reason, EditType editType, int orderIndex
) {
}
