package com.jordanfulawka.parsewell.service.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
public class S3ServiceImpl implements S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);
    private S3Presigner presigner;


    @Autowired
    public S3ServiceImpl(S3Presigner s3Presigner) {
        this.presigner = s3Presigner;
    }


    @Override
    public String createPresignedPutUrl(String key) {
        try {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket("parsewell-material-uploads")
                    .key(key)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        } catch (Exception e) {
            log.error("e: ", e);
            return "error";
        }
    }

    @Override
    public String createPresignedGetUrl(String key) {
        try {

            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket("parsewell-material-uploads")
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toExternalForm();


        } catch (Exception e) {
            log.error("e: ", e);
            return "error";
        }
    }
}
