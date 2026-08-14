package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeResponseDto;
import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.repository.BaseResumeRepository;
import com.jordanfulawka.parsewell.repository.UserRepository;
import com.jordanfulawka.parsewell.service.jobposting.PdfTextExtractor;
import com.jordanfulawka.parsewell.service.s3.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BaseResumeServiceImpl implements BaseResumeService{

    private final BaseResumeRepository baseResumeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PdfTextExtractor pdfTextExtractor;

    @Autowired
    public BaseResumeServiceImpl(BaseResumeRepository baseResumeRepository,
                                 UserRepository userRepository,
                                 S3Service s3Service,
                                 PdfTextExtractor pdfTextExtractor){
        this.baseResumeRepository = baseResumeRepository;
        this.userRepository = userRepository;
        this.s3Service = s3Service;
        this.pdfTextExtractor = pdfTextExtractor;
    }

    @Override
    public BaseResumeResponseDto createBaseResumeFromFile(String email, String fileName) throws IOException {

        User user = userRepository.findByEmail(email);
        String s3Key = "users/" + user.getId() + "/baseResume.pdf";

        byte[] pdfBytes = s3Service.downloadObject(s3Key);
        String content = pdfTextExtractor.extractText(pdfBytes);
//        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        BaseResume baseResume = baseResumeRepository.findByUser_Email(user.getEmail());
        if (baseResume == null) {
            baseResume = new BaseResume();
            baseResume.setUser(user);
        }

        baseResume.setContent(content);
        baseResume.setFileName(fileName);
        baseResume.setOriginalFileURL(s3Key);

        return mapToResponse(baseResumeRepository.save(baseResume));

    }

    @Override
    public BaseResumeResponseDto getBaseResumeForUser(String email) {
        BaseResume baseResume = baseResumeRepository.findByUser_Email(email);
        if (baseResume == null) {
            return null;
        }
        return mapToResponse(baseResume);
    }

    private BaseResumeResponseDto mapToResponse(BaseResume baseResume) {
        return new BaseResumeResponseDto(
                baseResume.getId(), baseResume.getUser().getId(),
                baseResume.getContent(), baseResume.getFileName(),
                baseResume.getOriginalFileURL(), baseResume.getCreatedAt()
        );
    }
}
