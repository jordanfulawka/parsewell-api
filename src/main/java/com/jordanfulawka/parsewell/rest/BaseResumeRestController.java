package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeRequestDto;
import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeResponseDto;
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
    public BaseResumeResponseDto createBaseResume(@RequestBody BaseResumeRequestDto baseResumeRequestDto) {
        return baseResumeService.createBaseResume(baseResumeRequestDto);
    }

    @GetMapping("")
    public List<BaseResumeResponseDto> getAllBaseResumes() {
        return baseResumeService.getAllBaseResumes();
    }
}
