package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeResponseDto;

import java.io.IOException;

public interface BaseResumeService {
    BaseResumeResponseDto createBaseResumeFromFile(String email, String fileName) throws IOException;
    BaseResumeResponseDto getBaseResumeForUser(String email);
}
