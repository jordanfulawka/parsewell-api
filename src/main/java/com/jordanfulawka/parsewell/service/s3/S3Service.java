package com.jordanfulawka.parsewell.service.s3;

public interface S3Service {
    public String createPresignedPutUrl(String key);
    public String createPresignedGetUrl(String key);
}
