package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.dto.jobpostings.JobPostingParseRequest;
import com.jordanfulawka.parsewell.dto.jobpostings.JobPostingResponse;
import com.jordanfulawka.parsewell.service.ai.ClaudeService;
import com.jordanfulawka.parsewell.service.jobposting.JobPostingFetchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/job-postings")
public class JobPostingRestController {

    private JobPostingFetchService jobPostingFetchService;
    private ClaudeService claudeService;

    public JobPostingRestController(JobPostingFetchService jobPostingFetchService,
                                    ClaudeService claudeService) {
        this.jobPostingFetchService = jobPostingFetchService;
        this.claudeService = claudeService;
    }

    @PostMapping("/parse")
    public JobPostingResponse parseJobSite(@RequestBody JobPostingParseRequest request) {
        String pageText = jobPostingFetchService.fetchWebpageHtml(request.url());
        return claudeService.parseJobPosting(pageText);
    }

}
