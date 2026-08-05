package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.ApplicationRequestDto;
import com.jordanfulawka.parsewell.entity.Application;

import java.util.List;

public interface ApplicationService {

    Application save(Application application);

    List<Application> getAllApplications();

    Application createApplication(ApplicationRequestDto applicationRequestDto);

}
