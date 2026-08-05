package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.EditSuggestionResponse;
import com.jordanfulawka.parsewell.entity.Application;
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
    public Application createApplication(@RequestBody ApplicationRequestDto applicationRequestDto) {
        return applicationService.createApplication(applicationRequestDto);
    }

    @GetMapping("")
    public List<Application> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @PostMapping("/{id}/generate-edits")
    public List<EditSuggestionResponse> generateEdits(@PathVariable UUID id) {
        return applicationService.generateEditSuggestions(id);
    }
}