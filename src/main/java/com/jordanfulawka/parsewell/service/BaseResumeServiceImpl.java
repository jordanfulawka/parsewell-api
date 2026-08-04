package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.repository.BaseResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseResumeServiceImpl implements BaseResumeService{

    private BaseResumeRepository baseResumeRepository;

    @Autowired
    public BaseResumeServiceImpl(BaseResumeRepository baseResumeRepository){
        this.baseResumeRepository = baseResumeRepository;
    }

    @Override
    public BaseResume save(BaseResume baseResume) {
        return baseResumeRepository.save(baseResume);
    }
}
