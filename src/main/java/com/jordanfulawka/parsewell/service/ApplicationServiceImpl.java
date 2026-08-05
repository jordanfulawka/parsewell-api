package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.ApplicationRequestDto;
import com.jordanfulawka.parsewell.entity.Application;
import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.repository.ApplicationRepository;
import com.jordanfulawka.parsewell.repository.BaseResumeRepository;
import com.jordanfulawka.parsewell.repository.UserRepository;
import com.jordanfulawka.parsewell.service.ai.ClaudeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService{

    private ApplicationRepository applicationRepository;
    private UserRepository userRepository;
    private BaseResumeRepository baseResumeRepository;
    private ClaudeService claudeService;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  UserRepository userRepository,
                                  BaseResumeRepository baseResumeRepository,
                                  ClaudeService claudeService) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.baseResumeRepository = baseResumeRepository;
        this.claudeService = claudeService;
    }

    @Override
    public Application save(Application application) {
        return applicationRepository.save(application);
    }

    @Override
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Override
    public Application createApplication(ApplicationRequestDto applicationRequestDto) {

        User user = userRepository.findById(applicationRequestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        BaseResume baseResume = baseResumeRepository.findById(applicationRequestDto.getBaseResumeId()).orElseThrow(() -> new EntityNotFoundException("Base resume not found"));

        Application application = new Application();
        application.setUser(user);
        application.setBaseResume(baseResume);
        application.setCompanyName(applicationRequestDto.getCompanyName());
        application.setRoleTitle(applicationRequestDto.getRoleTitle());
        application.setJobURL(applicationRequestDto.getJobURL());
        application.setJobDescription(applicationRequestDto.getJobDescription());
        application.setApplicationChannel(applicationRequestDto.getApplicationChannel());
        application.setApplicationStatus(applicationRequestDto.getApplicationStatus());
        application.setNotes(application.getNotes());

        return applicationRepository.save(application);
    }

}
