package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.applications.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.applications.ApplicationResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    List<ApplicationResponseDto> getAllApplications();
    ApplicationResponseDto createApplication(ApplicationRequestDto applicationRequestDto);
    List<EditSuggestionResponse> generateEditSuggestions(UUID applicationId);
    GeneratedCoverLetterResponse generateCoverLetter(UUID applicationId);
}
