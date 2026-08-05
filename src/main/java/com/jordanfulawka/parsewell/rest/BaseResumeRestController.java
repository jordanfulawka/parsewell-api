package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.BaseResumeRequestDto;
import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.service.BaseResumeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resumes")
public class BaseResumeRestController {

    private BaseResumeService baseResumeService;

    public BaseResumeRestController(BaseResumeService baseResumeService) {
        this.baseResumeService = baseResumeService;
    }

    @PostMapping("")
    public BaseResume createBaseResume(@RequestBody BaseResumeRequestDto baseResumeRequestDto) {
        return baseResumeService.createBaseResume(baseResumeRequestDto);
    }

    @GetMapping("")
    public List<BaseResume> getAllBaseResumes() {
        return baseResumeService.getAllBaseResumes();
    }
}
