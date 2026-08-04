package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.BaseResumeDto;
import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.repository.BaseResumeRepository;
import com.jordanfulawka.parsewell.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseResumeServiceImpl implements BaseResumeService{

    private BaseResumeRepository baseResumeRepository;
    private UserRepository userRepository;

    @Autowired
    public BaseResumeServiceImpl(BaseResumeRepository baseResumeRepository, UserRepository userRepository){
        this.baseResumeRepository = baseResumeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BaseResume save(BaseResume baseResume) {
        return baseResumeRepository.save(baseResume);
    }

    @Override
    public BaseResume createBaseResume(BaseResumeDto baseResumeDto) {
        User user = userRepository.findById(baseResumeDto.getUserId()).orElseThrow(() -> new EntityNotFoundException(("User not found")));

        BaseResume baseResume = new BaseResume();
        baseResume.setUser(user);
        baseResume.setLabel(baseResumeDto.getLabel());
        baseResume.setContent(baseResumeDto.getContent());
        baseResume.setOriginalFileURL(baseResumeDto.getOriginalFileURL());

        return baseResumeRepository.save(baseResume);
    }

    @Override
    public List<BaseResume> getAllBaseResumes() {
        return baseResumeRepository.findAll();
    }
}
