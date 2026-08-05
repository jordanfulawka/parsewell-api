package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.BaseResumeRequestDto;
import com.jordanfulawka.parsewell.entity.BaseResume;

import java.util.List;

public interface BaseResumeService {

    BaseResume createBaseResume(BaseResumeRequestDto baseResumeRequestDto);
    BaseResume save(BaseResume baseResume);
    List<BaseResume> getAllBaseResumes();

}
