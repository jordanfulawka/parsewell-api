package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.entity.Application;
import com.jordanfulawka.parsewell.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationRestController {

    private ApplicationService applicationService;

    @Autowired
    public ApplicationRestController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("")
    public Application addApplication(@RequestBody Application application) {
        return applicationService.save(application);
    }
}
