package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeRequestDto;
import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeResponseDto;
import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.repository.BaseResumeRepository;
import com.jordanfulawka.parsewell.repository.UserRepository;
import com.jordanfulawka.parsewell.service.jobposting.PdfTextExtractor;
import com.jordanfulawka.parsewell.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public BaseResumeResponseDto createBaseResume(BaseResumeRequestDto baseResumeRequestDto) {
        User user = userRepository.findById(baseResumeRequestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException(("User not found")));

        BaseResume baseResume = new BaseResume();
        baseResume.setUser(user);
        baseResume.setContent(baseResumeRequestDto.getContent());
        baseResume.setOriginalFileURL(baseResumeRequestDto.getOriginalFileURL());

        baseResume = baseResumeRepository.save(baseResume);
        return mapToResponse(baseResume);
    }

    @Override
    public List<BaseResumeResponseDto> getAllBaseResumes() {
        List<BaseResume> baseResumes = baseResumeRepository.findAll();
        List<BaseResumeResponseDto> responses = new ArrayList<>();

        for(BaseResume baseResume : baseResumes) {
            responses.add(mapToResponse(baseResume));
        }
        return responses;
    }

    @Override
    public BaseResumeResponseDto createBaseResumeFromFile(UUID userId, String label, String s3Key) throws IOException {
        byte[] pdfBytes = s3Service.downloadObject(s3Key);
        String content = pdfTextExtractor.extractText(pdfBytes);
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        BaseResume baseResume = baseResumeRepository.findByUser_Email(user.getEmail());
        if (baseResume == null) {
            baseResume = new BaseResume();
            baseResume.setUser(user);
        }

        baseResume.setContent(content);
        baseResume.setOriginalFileURL(s3Key);

        return mapToResponse(baseResumeRepository.save(baseResume));

    }

    @Override
    public BaseResumeResponseDto getBaseResumeForUser(String email) {
        BaseResume baseResume = baseResumeRepository.findByUser_Email(email);
        if(baseResume == null) {
            throw new EntityNotFoundException("Base resume not found for user with email: " + email);
        }
        return mapToResponse(baseResume);
    }

    private BaseResumeResponseDto mapToResponse(BaseResume baseResume) {
        return new BaseResumeResponseDto(
                baseResume.getId(), baseResume.getUser().getId(),
                baseResume.getContent(), baseResume.getOriginalFileURL(), baseResume.getCreatedAt()
        );
    }
}
