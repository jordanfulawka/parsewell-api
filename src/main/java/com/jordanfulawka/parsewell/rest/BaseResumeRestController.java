package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeResponseDto;
import com.jordanfulawka.parsewell.service.BaseResumeService;
import com.jordanfulawka.parsewell.service.s3.S3Service;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public BaseResumeResponseDto uploadMyBaseResume(@AuthenticationPrincipal UserDetails userDetails) throws IOException {
        return baseResumeService.createBaseResumeFromFile(userDetails.getUsername());
    }

    @GetMapping("/base/upload-url")
    public String getUploadUrl(@AuthenticationPrincipal UserDetails userDetails) {
        return s3Service.createPresignedPutUrl(userDetails.getUsername());
    }
}
