package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.entity.Application;
import com.jordanfulawka.parsewell.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationServiceImpl implements ApplicationService{

    private ApplicationRepository applicationRepository;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Application save(Application application) {
        return applicationRepository.save(application);
    }
}
