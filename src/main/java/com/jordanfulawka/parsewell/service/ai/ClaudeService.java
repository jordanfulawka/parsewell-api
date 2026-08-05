package com.jordanfulawka.parsewell.service.ai;

import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionAiResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;

import java.util.List;

public interface ClaudeService {

    List<EditSuggestionAiResponseDto> generateEditSuggestions(String baseResumeContent, String jobDescription);

    GeneratedCoverLetterResponse generateCoverLetter(String baseResumeContent, String jobDescription, List<EditSuggestionResponse> suggestions);
}
