package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.applications.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.applications.ApplicationResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;
import com.jordanfulawka.parsewell.dto.finalmaterials.CoverLetterRequestDto;
import com.jordanfulawka.parsewell.dto.finalmaterials.FinalMaterialDto;
import com.jordanfulawka.parsewell.dto.finalmaterials.ResumeRequestDto;
import com.jordanfulawka.parsewell.dto.jobpostings.JobPostingResponse;
import com.jordanfulawka.parsewell.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationRestController {

    private ApplicationService applicationService;

    @Autowired
    public ApplicationRestController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("")
    public List<ApplicationResponseDto> getApplications(@AuthenticationPrincipal UserDetails userDetails) {
        return applicationService.getAllApplications(userDetails.getUsername());
    }

    @PostMapping("")
    public ApplicationResponseDto createApplication(@RequestBody ApplicationRequestDto applicationRequestDto) {
        return applicationService.createApplication(applicationRequestDto);
    }

    @PostMapping("/create-request")
    public ApplicationRequestDto createApplicationRequest(@RequestBody JobPostingResponse jobPostingResponse, @AuthenticationPrincipal UserDetails userDetails) {
        return applicationService.createApplicationRequest(jobPostingResponse, userDetails);
    }


    @PutMapping("/{id}")
    public ApplicationResponseDto updateApplication(@RequestBody ApplicationResponseDto applicationResponseDto, @PathVariable UUID id) {
        return applicationService.updateApplication(applicationResponseDto);
    }

    @GetMapping("/{id}")
    public ApplicationResponseDto findApplicationById(@PathVariable UUID id) {
        return applicationService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        applicationService.deleteApplication(id);
    }

    @GetMapping("/{id}/generate-edits")
    public List<EditSuggestionResponse> generateEdits(@PathVariable UUID id) {
        return applicationService.generateEditSuggestions(id);
    }

    @GetMapping("/{id}/edits")
    public List<EditSuggestionResponse> getEditSuggestions(@PathVariable UUID id) {
        return applicationService.getEditSuggestionByApplicationId(id);
    }

    @GetMapping("/{id}/generate-cover-letter")
    public GeneratedCoverLetterResponse generateCoverLetter(@PathVariable UUID id) {
        return applicationService.generateCoverLetter(id);
    }

    @GetMapping("/{id}/cover-letter")
    public GeneratedCoverLetterResponse getCoverLetter(@PathVariable UUID id) {
        return applicationService.getCoverLetterByApplicationId(id);
    }

    @PostMapping("/{id}/upload-final-materials")
    public FinalMaterialDto uploadFinalMaterials(@PathVariable UUID id, @RequestBody FinalMaterialDto finalMaterialDto) {
        return applicationService.saveFinalMaterials(id, finalMaterialDto);
    }

    @PostMapping("/{id}/upload-resume")
    public FinalMaterialDto uploadResume(@PathVariable UUID id, @RequestBody ResumeRequestDto resumeRequestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return applicationService.saveResume(id, resumeRequestDto, userDetails.getUsername());
    }

    @PostMapping("/{id}/upload-cover-letter")
    public FinalMaterialDto uploadCoverLetter(@PathVariable UUID id, @RequestBody CoverLetterRequestDto coverLetterRequestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return applicationService.saveCoverLetter(id, coverLetterRequestDto, userDetails.getUsername());
    }

    @GetMapping("/{id}/materials")
    public FinalMaterialDto getFinalMaterials(@PathVariable UUID id) {
        return applicationService.getFinalMaterials(id);
    }

    @GetMapping("/{id}/materials/upload-url")
    public String getUploadUrl(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id, @RequestParam String type) {
        return applicationService.createUploadUrl(userDetails.getUsername(), id, type);
    }

    @GetMapping("/{id}/materials/download-url")
    public String getDownloadUrl(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id, @RequestParam String type) {
        return applicationService.createDownloadUrl(userDetails.getUsername(), id, type);
    }
}