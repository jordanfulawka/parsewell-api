package com.jordanfulawka.parsewell.service;

public interface S3Service {
    public String createPresignedUrl(String key);
}
