package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.BaseResumeDto;
import com.jordanfulawka.parsewell.entity.BaseResume;

import java.util.List;

public interface BaseResumeService {

    BaseResume createBaseResume(BaseResumeDto baseResumeDto);
    BaseResume save(BaseResume baseResume);
    List<BaseResume> getAllBaseResumes();

}
