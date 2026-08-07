package com.jordanfulawka.parsewell.rest;

import com.jordanfulawka.parsewell.service.s3.S3Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/s3")
public class S3RestController {

    private S3Service s3Service;

    public S3RestController(S3Service s3Service) {
        this.s3Service = s3Service;
    }


    @PostMapping("/presignPut")
    public String getPresignedURL(@RequestBody String key) {
        return s3Service.createPresignedPutUrl(key);
    }

    @PostMapping("/presignGet")
    public String getPresignedGetUrl(@RequestBody String key) {
        return s3Service.createPresignedGetUrl(key);
    }
}
