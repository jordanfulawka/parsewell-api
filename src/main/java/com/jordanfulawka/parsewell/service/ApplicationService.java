package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.EditSuggestionResponse;
import com.jordanfulawka.parsewell.entity.Application;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {

    Application save(Application application);

    List<Application> getAllApplications();

    Application createApplication(ApplicationRequestDto applicationRequestDto);

    List<EditSuggestionResponse> generateEditSuggestions(UUID appliationId);

}
