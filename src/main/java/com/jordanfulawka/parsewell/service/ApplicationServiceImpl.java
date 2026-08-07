package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.applications.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.applications.ApplicationResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionAiResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;
import com.jordanfulawka.parsewell.dto.finalmaterials.FinalMaterialDto;
import com.jordanfulawka.parsewell.entity.*;
import com.jordanfulawka.parsewell.entity.enums.EditType;
import com.jordanfulawka.parsewell.repository.*;
import com.jordanfulawka.parsewell.service.ai.ClaudeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService{

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final BaseResumeRepository baseResumeRepository;
    private final ClaudeService claudeService;
    private final EditSuggestionRepository editSuggestionRepository;
    private final GeneratedCoverLetterRepository generatedCoverLetterRepository;
    private final FinalMaterialRepository finalMaterialRepository;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  UserRepository userRepository,
                                  BaseResumeRepository baseResumeRepository,
                                  ClaudeService claudeService,
                                  EditSuggestionRepository editSuggestionRepository,
                                  GeneratedCoverLetterRepository generatedCoverLetterRepository,
                                  FinalMaterialRepository finalMaterialRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.baseResumeRepository = baseResumeRepository;
        this.claudeService = claudeService;
        this.editSuggestionRepository = editSuggestionRepository;
        this.generatedCoverLetterRepository = generatedCoverLetterRepository;
        this.finalMaterialRepository = finalMaterialRepository;
    }

    @Override
    public List<ApplicationResponseDto> getAllApplications() {

        List<Application> applications = applicationRepository.findAll();
        List<ApplicationResponseDto> responses = new ArrayList<>();

        for(Application application : applications) {
            responses.add(mapToResponse(application));
        }

        return responses;
    }

    @Override
    public ApplicationResponseDto createApplication(ApplicationRequestDto applicationRequestDto) {

        User user = userRepository.findById(applicationRequestDto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found"));
        BaseResume baseResume = baseResumeRepository.findById(applicationRequestDto.getBaseResumeId()).orElseThrow(() -> new EntityNotFoundException("Base resume not found"));

        Application application = new Application();
        application.setUser(user);
        application.setBaseResume(baseResume);
        application.setCompanyName(applicationRequestDto.getCompanyName());
        application.setRoleTitle(applicationRequestDto.getRoleTitle());
        application.setLocation(applicationRequestDto.getLocation());
        application.setJobURL(applicationRequestDto.getJobURL());
        application.setJobDescription(applicationRequestDto.getJobDescription());
        application.setApplicationChannel(applicationRequestDto.getApplicationChannel());
        application.setApplicationStatus(applicationRequestDto.getApplicationStatus());
        application.setNotes(applicationRequestDto.getNotes());

        application = applicationRepository.save(application);

        return mapToResponse(application);
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

        List<EditSuggestion> saved = editSuggestionRepository.saveAll(suggestions);

        List<EditSuggestionResponse> responses = new ArrayList<>();
        for(EditSuggestion suggestion : suggestions) {
            responses.add(mapToResponse(suggestion));
        }
        return responses;
    }

    @Override
    public GeneratedCoverLetterResponse generateCoverLetter(UUID applicationId) {

        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new EntityNotFoundException("Application cannot be found"));
        BaseResume baseResume = application.getBaseResume();
        List<EditSuggestion> rawSuggestions = editSuggestionRepository.findAllByApplicationId(applicationId);

        List<EditSuggestionResponse> suggestions = new ArrayList<>();

        for(EditSuggestion suggestion : rawSuggestions) {
            suggestions.add(mapToResponse(suggestion));
        }

        GeneratedCoverLetterResponse generatedCoverLetterResponse = claudeService.generateCoverLetter(
                baseResume.getContent(),
                application.getJobDescription(),
                suggestions
        );

        GeneratedCoverLetter generatedCoverLetter = mapToEntity(generatedCoverLetterResponse, application);
        generatedCoverLetterRepository.save(generatedCoverLetter);

        return generatedCoverLetterResponse;
    }

    @Override
    public FinalMaterialDto saveFinalMaterials(UUID applicationId, FinalMaterialDto dto) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new EntityNotFoundException("Application not found"));

        FinalMaterial finalMaterial = new FinalMaterial();
        finalMaterial.setApplication(application);
        finalMaterial.setResumeURL(dto.resumeURL());
        finalMaterial.setCoverLetterURL(dto.coverLetterURL());
        finalMaterialRepository.save(finalMaterial);

        return dto;
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
                editSuggestion.getId(), editSuggestion.getApplication().getId() ,editSuggestion.getSection(), editSuggestion.getBeforeText(),
                editSuggestion.getAfterText(), editSuggestion.getReason(), editSuggestion.getEditType(), editSuggestion.getOrderIndex()
        );
    }

    private ApplicationResponseDto mapToResponse(Application application) {
        return new ApplicationResponseDto(
                application.getId(), application.getUser().getId(),
                application.getBaseResume().getId(), application.getCompanyName(),
                application.getRoleTitle(), application.getLocation(), application.getJobURL(), application.getJobDescription(),
                application.getApplicationChannel(), application.getApplicationStatus(), application.getNotes(),
                application.getCreatedAt(), application.getUpdatedAt()
        );
    }

    private GeneratedCoverLetter mapToEntity(GeneratedCoverLetterResponse generatedCoverLetterResponse, Application application) {
        GeneratedCoverLetter generatedCoverLetter = new GeneratedCoverLetter();
        generatedCoverLetter.setApplication(application);
        generatedCoverLetter.setContent(generatedCoverLetterResponse.content());
        return generatedCoverLetter;
    }

}
