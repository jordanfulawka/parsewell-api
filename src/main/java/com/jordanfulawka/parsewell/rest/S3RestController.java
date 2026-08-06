package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.service.S3Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/s3")
public class S3RestController {

    private S3Service s3Service;

    public S3RestController(S3Service s3Service) {
        this.s3Service = s3Service;
    }


    @GetMapping("/presign")
    public String getPresignedURL(@RequestBody String key) {
        return s3Service.createPresignedUrl(key);
    }
}
