package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.EditSuggestionAiResponseDto;
import com.jordanfulawka.parsewell.dto.EditSuggestionResponse;
import com.jordanfulawka.parsewell.entity.Application;
import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.entity.EditSuggestion;
import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.entity.enums.EditType;
import com.jordanfulawka.parsewell.repository.ApplicationRepository;
import com.jordanfulawka.parsewell.repository.BaseResumeRepository;
import com.jordanfulawka.parsewell.repository.EditSuggestionRepository;
import com.jordanfulawka.parsewell.repository.UserRepository;
import com.jordanfulawka.parsewell.service.ai.ClaudeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService{

    private ApplicationRepository applicationRepository;
    private UserRepository userRepository;
    private BaseResumeRepository baseResumeRepository;
    private ClaudeService claudeService;
    private EditSuggestionRepository editSuggestionRepository;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  UserRepository userRepository,
                                  BaseResumeRepository baseResumeRepository,
                                  ClaudeService claudeService,
                                  EditSuggestionRepository editSuggestionRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.baseResumeRepository = baseResumeRepository;
        this.claudeService = claudeService;
        this.editSuggestionRepository = editSuggestionRepository;
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

    @Override
    public List<EditSuggestionResponse> generateEditSuggestions(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new EntityNotFoundException("Application not found"));

        BaseResume baseResume = application.getBaseResume();

        List<EditSuggestionAiResponseDto> aiResponse = claudeService.generateEditSuggestions(
                baseResume.getContent(),
                application.getJobDescription()
        );

        List<EditSuggestion> suggestions = new ArrayList<>();
        for(EditSuggestionAiResponseDto response : aiResponse) {
            suggestions.add(mapToEntity(response, application));
        }

        List<EditSuggestion> saved =editSuggestionRepository.saveAll(suggestions);

        List<EditSuggestionResponse> responses = new ArrayList<>();
        for(EditSuggestion suggestion : suggestions) {
            responses.add(mapToResponse(suggestion));
        }
        return responses;
    }

    private EditSuggestion mapToEntity(EditSuggestionAiResponseDto dto, Application application) {
        EditSuggestion editSuggestion = new EditSuggestion();
        editSuggestion.setApplication(application);
        editSuggestion.setSection(dto.getSection());
        editSuggestion.setBeforeText(dto.getBeforeText());
        editSuggestion.setAfterText(dto.getAfterText());
        editSuggestion.setReason(dto.getReason());
        editSuggestion.setEditType(EditType.valueOf(dto.getEditType()));
        editSuggestion.setOrderIndex(dto.getOrderIndex());
        return editSuggestion;
    }

    private EditSuggestionResponse mapToResponse(EditSuggestion editSuggestion) {
        return new EditSuggestionResponse(
                editSuggestion.getId(), editSuggestion.getSection(), editSuggestion.getBeforeText(),
                editSuggestion.getAfterText(), editSuggestion.getReason(), editSuggestion.getEditType(), editSuggestion.getOrderIndex()
        );
    }

}
