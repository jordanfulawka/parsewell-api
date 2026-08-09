package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.applications.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.applications.ApplicationResponseDto;
import com.jordanfulawka.parsewell.dto.jobpostings.JobPostingParseRequest;
import com.jordanfulawka.parsewell.dto.jobpostings.JobPostingResponse;
import com.jordanfulawka.parsewell.service.ApplicationService;
import com.jordanfulawka.parsewell.service.ai.ClaudeService;
import com.jordanfulawka.parsewell.service.jobposting.JobPostingFetchService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/job-postings")
public class JobPostingRestController {

    private JobPostingFetchService jobPostingFetchService;
    private ClaudeService claudeService;
    private ApplicationService applicationService;

    public JobPostingRestController(JobPostingFetchService jobPostingFetchService,
                                    ClaudeService claudeService,
                                    ApplicationService applicationService) {
        this.jobPostingFetchService = jobPostingFetchService;
        this.claudeService = claudeService;
        this.applicationService = applicationService;
    }

    @PostMapping("/parse")
    public ApplicationResponseDto parseJobSite(@RequestBody JobPostingParseRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        String pageText = jobPostingFetchService.fetchWebpageHtml(request.url());
        if (pageText.isBlank()) {
            throw new IllegalArgumentException("Could not fetch content from the provided job posting URL.");
        }
        JobPostingResponse jobPostingResponse = claudeService.parseJobPosting(pageText);
        ApplicationRequestDto applicationRequestDto = applicationService.createApplicationRequest(jobPostingResponse, userDetails);
        return applicationService.createApplication(applicationRequestDto);
    }

}
