package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.ApplicationDto;
import com.jordanfulawka.parsewell.entity.Application;
import com.jordanfulawka.parsewell.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationRestController {

    private ApplicationService applicationService;

    @Autowired
    public ApplicationRestController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("")
    public Application createApplication(@RequestBody ApplicationDto applicationDto) {
        return applicationService.createApplication(applicationDto);
    }

    @GetMapping("")
    public List<Application> getAllApplications() {
        return applicationService.getAllApplications();
    }


}