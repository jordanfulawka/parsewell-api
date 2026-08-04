package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.service.BaseResumeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/resumes")
public class BaseResumeRestController {

    private BaseResumeService baseResumeService;

    public BaseResumeRestController(BaseResumeService baseResumeService) {
        this.baseResumeService = baseResumeService;
    }

    @PostMapping("")
    public BaseResume addBaseResume(@RequestBody BaseResume baseResume) {
        return baseResumeService.save(baseResume);
    }
}
