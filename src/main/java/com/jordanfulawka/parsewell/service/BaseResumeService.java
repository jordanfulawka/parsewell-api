package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeRequestDto;
import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeResponseDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BaseResumeService {
    BaseResumeResponseDto createBaseResume(BaseResumeRequestDto baseResumeRequestDto);
    List<BaseResumeResponseDto> getAllBaseResumes();
    BaseResumeResponseDto createBaseResumeFromFile(UUID userId, String label, String s3Key) throws IOException;
    BaseResumeResponseDto getBaseResumeForUser(String email);
}
