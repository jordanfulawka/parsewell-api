package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeRequestDto;
import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeResponseDto;
import com.jordanfulawka.parsewell.service.BaseResumeService;
import com.jordanfulawka.parsewell.service.s3.S3Service;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/resumes")
public class BaseResumeRestController {

    private BaseResumeService baseResumeService;
    private S3Service s3Service;

    public BaseResumeRestController(BaseResumeService baseResumeService,
                                    S3Service s3Service) {
        this.baseResumeService = baseResumeService;
        this.s3Service = s3Service;
    }

    @GetMapping("/me")
    public BaseResumeResponseDto getMyBaseResume(@AuthenticationPrincipal UserDetails userDetails) {
        return baseResumeService.getBaseResumeForUser(userDetails.getUsername());
    }

    @PutMapping("/me")
    public BaseResumeResponseDto uploadMyBaseResume(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BaseResumeRequestDto baseResumeRequestDto) throws IOException {
        return baseResumeService.createBaseResumeFromFile(userDetails.getUsername(), baseResumeRequestDto.fileName());
    }

    @GetMapping("/base/upload-url")
    public String getUploadUrl(@AuthenticationPrincipal UserDetails userDetails) {
        return s3Service.createPresignedPutUrl(userDetails.getUsername());
    }
}
