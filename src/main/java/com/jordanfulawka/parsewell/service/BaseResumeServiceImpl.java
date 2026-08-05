package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeRequestDto;
import com.jordanfulawka.parsewell.dto.baseresumes.BaseResumeResponseDto;
import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.repository.BaseResumeRepository;
import com.jordanfulawka.parsewell.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public BaseResumeResponseDto createBaseResume(BaseResumeRequestDto baseResumeRequestDto) {
        User user = userRepository.findById(baseResumeRequestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException(("User not found")));

        BaseResume baseResume = new BaseResume();
        baseResume.setUser(user);
        baseResume.setLabel(baseResumeRequestDto.getLabel());
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

    private BaseResumeResponseDto mapToResponse(BaseResume baseResume) {
        return new BaseResumeResponseDto(
                baseResume.getId(), baseResume.getUser().getId(),
                baseResume.getLabel(), baseResume.getContent(),
                baseResume.getOriginalFileURL(), baseResume.getCreatedAt()
        );
    }
}
