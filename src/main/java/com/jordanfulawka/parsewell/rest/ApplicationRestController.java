package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.applications.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.applications.ApplicationResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;
import com.jordanfulawka.parsewell.dto.finalmaterials.FinalMaterialDto;
import com.jordanfulawka.parsewell.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("")
    public List<ApplicationResponseDto> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @PostMapping("/{id}/generate-edits")
    public List<EditSuggestionResponse> generateEdits(@PathVariable UUID id) {
        return applicationService.generateEditSuggestions(id);
    }

    @PostMapping("/{id}/generate-cover-letter")
    public GeneratedCoverLetterResponse generateCoverLetter(@PathVariable UUID id) {
        return applicationService.generateCoverLetter(id);
    }

    @PostMapping("/{id}/upload-final-materials")
    public FinalMaterialDto uploadFinalMaterials(@PathVariable UUID id, @RequestBody FinalMaterialDto finalMaterialDto) {
        return applicationService.saveFinalMaterials(id, finalMaterialDto);
    }
}