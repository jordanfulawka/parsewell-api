package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.applications.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.applications.ApplicationResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;
import com.jordanfulawka.parsewell.dto.finalmaterials.CoverLetterRequestDto;
import com.jordanfulawka.parsewell.dto.finalmaterials.FinalMaterialDto;
import com.jordanfulawka.parsewell.dto.finalmaterials.ResumeRequestDto;
import com.jordanfulawka.parsewell.dto.jobpostings.JobPostingResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    ApplicationResponseDto createApplication(ApplicationRequestDto applicationRequestDto);
    ApplicationRequestDto createApplicationRequest(JobPostingResponse jobPostingResponse, UserDetails userDetails);
    List<ApplicationResponseDto> getAllApplications(String email);
    List<EditSuggestionResponse> generateEditSuggestions(UUID applicationId);
    GeneratedCoverLetterResponse generateCoverLetter(UUID applicationId);
    GeneratedCoverLetterResponse getCoverLetterByApplicationId(UUID applicationId);
    FinalMaterialDto saveFinalMaterials(UUID applicationId, FinalMaterialDto finalMaterialDto);
    ApplicationResponseDto findById(UUID applicationId);
    List<EditSuggestionResponse> getEditSuggestionByApplicationId(UUID applicationId);
    ApplicationResponseDto updateApplication(ApplicationResponseDto applicationResponseDto);
    String createUploadUrl(String email, UUID applicationId, String type);
    String createDownloadUrl(String email, UUID applicationId, String type);
    FinalMaterialDto saveResume(UUID applicationId, ResumeRequestDto dto, String email);
    FinalMaterialDto saveCoverLetter(UUID applicationId, CoverLetterRequestDto dto, String email);
    FinalMaterialDto getFinalMaterials(UUID applicationId);
}
