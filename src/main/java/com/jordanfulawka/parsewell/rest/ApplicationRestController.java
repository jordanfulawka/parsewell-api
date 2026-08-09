package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.applications.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.applications.ApplicationResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;
import com.jordanfulawka.parsewell.dto.finalmaterials.FinalMaterialDto;
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
        System.out.println(applicationResponseDto);
        return applicationService.updateApplication(applicationResponseDto);
    }

    @GetMapping("/{id}")
    public ApplicationResponseDto findApplicationById(@PathVariable UUID id) {
        return applicationService.findById(id);
    }

    @GetMapping("/{id}/generate-edits")
    public List<EditSuggestionResponse> generateEdits(@PathVariable UUID id) {
        return applicationService.generateEditSuggestions(id);
    }

    @GetMapping("/{id}/edits")
    public List<EditSuggestionResponse> getEditSuggestions(@PathVariable UUID id) {
        return applicationService.getEditSuggestionByApplicationId(id);
    }

    @PostMapping("/{id}/generate-cover-letter")
    public GeneratedCoverLetterResponse generateCoverLetter(@PathVariable UUID id) {
        return applicationService.generateCoverLetter(id);
    }

    @PostMapping("/{id}/upload-final-materials")
    public FinalMaterialDto uploadFinalMaterials(@PathVariable UUID id, @RequestBody FinalMaterialDto finalMaterialDto) {
        return applicationService.saveFinalMaterials(id, finalMaterialDto);
    }

    @PostMapping("/{id}/materials/upload-url")
    public String getUploadUrl(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id, @RequestBody String type) {
        return "";
    }
}