package com.jordanfulawka.parsewell.service.s3;

import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3ServiceImpl.class);
    private final S3Presigner presigner;
    private final S3Client client;
    private final UserRepository userRepository;


    @Autowired
    public S3ServiceImpl(S3Client s3Client, S3Presigner s3Presigner,
                         UserRepository userRepository) {
        this.client = s3Client;
        this.presigner = s3Presigner;
        this.userRepository = userRepository;
    }


    @Override
    public String createPresignedPutUrl(String email) {
        User user = userRepository.findByEmail(email);
        UUID uuid = UUID.randomUUID();
        try {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket("parsewell-material-uploads")
                    .key("resumes/" + user.getId() + "/base-resume.pdf")
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
    public String createPresignedPutUrl(String email, UUID applicationId, String type) {
        User user = userRepository.findByEmail(email);
        UUID uuid = UUID.randomUUID();
        try {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket("parsewell-material-uploads")
                    .key("final-materials/" + user.getId() + "/" + String.valueOf(applicationId) + "/" + type + ".pdf")
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


    @Override
    public byte[] downloadObject(String key) {

        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .bucket("parsewell-material-uploads")
                .key(key)
                .build();

        return client.getObjectAsBytes(objectRequest).asByteArray();
    }


}
