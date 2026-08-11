package com.jordanfulawka.parsewell.service.s3;

import java.util.UUID;

public interface S3Service {
    public String createPresignedPutUrl(String email);
    public String createPresignedPutUrl(String email, UUID applicationId, String type);
    public String createPresignedGetUrl(String email);
    public String createPresignedGetUrl(String email, UUID applicationId, String type);
    byte[] downloadObject(String key);
}
