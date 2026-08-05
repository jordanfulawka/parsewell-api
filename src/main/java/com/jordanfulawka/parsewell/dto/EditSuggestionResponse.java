package com.jordanfulawka.parsewell.dto;

import com.jordanfulawka.parsewell.entity.enums.EditType;

import java.util.UUID;

public record EditSuggestionResponse(
        UUID id, String section, String beforeText, String afterText, String reason, EditType editType, int orderIndex
) {
}
