package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.ApplicationDto;
import com.jordanfulawka.parsewell.entity.Application;
import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.repository.ApplicationRepository;
import com.jordanfulawka.parsewell.repository.BaseResumeRepository;
import com.jordanfulawka.parsewell.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService{

    private ApplicationRepository applicationRepository;
    private UserRepository userRepository;
    private BaseResumeRepository baseResumeRepository;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  UserRepository userRepository,
                                  BaseResumeRepository baseResumeRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.baseResumeRepository = baseResumeRepository;
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
    public Application createApplication(ApplicationDto applicationDto) {

        User user = userRepository.findById(applicationDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        BaseResume baseResume = baseResumeRepository.findById(applicationDto.getBaseResumeId()).orElseThrow(() -> new EntityNotFoundException("Base resume not found"));

        Application application = new Application();
        application.setUser(user);
        application.setBaseResume(baseResume);
        application.setCompanyName(applicationDto.getCompanyName());
        application.setRoleTitle(applicationDto.getRoleTitle());
        application.setJobURL(applicationDto.getJobURL());
        application.setJobDescription(applicationDto.getJobDescription());
        application.setApplicationChannel(applicationDto.getApplicationChannel());
        application.setApplicationStatus(applicationDto.getApplicationStatus());
        application.setNotes(application.getNotes());

        return applicationRepository.save(application);
    }
}
