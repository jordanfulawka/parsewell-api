package com.jordanfulawka.parsewell.service.s3;

import java.util.UUID;

public interface S3Service {
    public String createPresignedPutUrl(String email);
    public String createPresignedPutUrl(String email, UUID applicationId, String type);
    public String createPresignedGetUrl(String key);
    byte[] downloadObject(String key);
}
