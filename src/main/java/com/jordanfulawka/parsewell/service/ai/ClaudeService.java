package com.jordanfulawka.parsewell.service.ai;

import com.jordanfulawka.parsewell.dto.EditSuggestionAiResponseDto;

import java.util.List;

public interface ClaudeService {

    List<EditSuggestionAiResponseDto> generateEditSuggestions(String baseResumeContent, String jobDescription);
//    Message generateEditSuggestions();
    String generateCoverLetter(String baseResumeContent, String jobDescription);
}
