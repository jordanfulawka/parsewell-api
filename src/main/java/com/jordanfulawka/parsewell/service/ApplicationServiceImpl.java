package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.applications.ApplicationRequestDto;
import com.jordanfulawka.parsewell.dto.applications.ApplicationResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionAiResponseDto;
import com.jordanfulawka.parsewell.dto.editsuggestions.EditSuggestionResponse;
import com.jordanfulawka.parsewell.dto.editsuggestions.GeneratedCoverLetterResponse;
import com.jordanfulawka.parsewell.dto.finalmaterials.*;
import com.jordanfulawka.parsewell.dto.jobpostings.JobPostingResponse;
import com.jordanfulawka.parsewell.entity.*;
import com.jordanfulawka.parsewell.entity.enums.ApplicationStatus;
import com.jordanfulawka.parsewell.entity.enums.EditType;
import com.jordanfulawka.parsewell.repository.*;
import com.jordanfulawka.parsewell.service.ai.ClaudeService;
import com.jordanfulawka.parsewell.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final S3Service s3Service;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository,
                                  UserRepository userRepository,
                                  BaseResumeRepository baseResumeRepository,
                                  ClaudeService claudeService,
                                  EditSuggestionRepository editSuggestionRepository,
                                  GeneratedCoverLetterRepository generatedCoverLetterRepository,
                                  FinalMaterialRepository finalMaterialRepository,
                                  S3Service s3Service) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.baseResumeRepository = baseResumeRepository;
        this.claudeService = claudeService;
        this.editSuggestionRepository = editSuggestionRepository;
        this.generatedCoverLetterRepository = generatedCoverLetterRepository;
        this.finalMaterialRepository = finalMaterialRepository;
        this.s3Service = s3Service;
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
        application.setApplicationStatus(ApplicationStatus.DRAFT);
        application.setNotes(applicationRequestDto.getNotes());

        application = applicationRepository.save(application);

        return mapToResponse(application);
    }



    @Override
    public ApplicationRequestDto createApplicationRequest(JobPostingResponse jobPostingResponse, UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername());
        BaseResume baseResume = baseResumeRepository.findByUser_Email(user.getEmail());

        ApplicationRequestDto applicationRequestDto = new ApplicationRequestDto();
        applicationRequestDto.setUserId(user.getId());
        applicationRequestDto.setBaseResumeId(baseResume.getId());
        applicationRequestDto.setCompanyName(jobPostingResponse.getCompanyName());
        applicationRequestDto.setRoleTitle(jobPostingResponse.getRoleTitle());
        applicationRequestDto.setLocation(jobPostingResponse.getLocation());
        applicationRequestDto.setJobURL(jobPostingResponse.getJobUrl());
        applicationRequestDto.setJobDescription(jobPostingResponse.getJobDescription());

        return applicationRequestDto;
    }

    @Override
    public List<ApplicationResponseDto> getAllApplications(String email) {
        User user = userRepository.findByEmail(email);
        List<Application> applications = applicationRepository.findAllByUserIdOrderByUpdatedAtDesc(user.getId());

        List<ApplicationResponseDto> applicationResponses = new ArrayList<>();

        for(Application application : applications) {
            applicationResponses.add(mapToResponse(application));
        }

        return applicationResponses;
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

        GeneratedCoverLetter generatedCoverLetter = generatedCoverLetterRepository.findByApplicationId(applicationId);
        if(generatedCoverLetter == null) {
            generatedCoverLetter = new GeneratedCoverLetter();
        }

        mapToEntity(generatedCoverLetter, generatedCoverLetterResponse, application);
        generatedCoverLetterRepository.save(generatedCoverLetter);

        return generatedCoverLetterResponse;
    }

    @Override
    public GeneratedCoverLetterResponse getCoverLetterByApplicationId(UUID applicationId) {
        GeneratedCoverLetter generatedCoverLetter = generatedCoverLetterRepository.findByApplicationId(applicationId);

        if(generatedCoverLetter == null) {
            throw new EntityNotFoundException("Cover letter not found for application " + applicationId);
        }

        return new GeneratedCoverLetterResponse(generatedCoverLetter.getContent());
    }

    @Override
    public FinalMaterialDto saveFinalMaterials(UUID applicationId, FinalMaterialDto dto) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new EntityNotFoundException("Application not found"));


        FinalMaterial finalMaterial = finalMaterialRepository.findByApplicationId(applicationId);
        if(finalMaterial == null) {
            finalMaterial = new FinalMaterial();
        }

        finalMaterial.setApplication(application);
        finalMaterial.setResumeKey(dto.resumeKey());
        finalMaterial.setResumeFilename(dto.resumeFilename());
        finalMaterial.setCoverLetterKey(dto.coverLetterKey());
        finalMaterial.setCoverLetterFilename(dto.coverLetterFilename());
        finalMaterialRepository.save(finalMaterial);

        return dto;
    }

    public FinalMaterialDto saveResume(UUID applicationId, ResumeRequestDto dto, String email) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new EntityNotFoundException("Application nto found"));
        User user = userRepository.findByEmail(email);

        FinalMaterial finalMaterial = finalMaterialRepository.findByApplicationId(applicationId);
        if(finalMaterial == null) {
            finalMaterial = new FinalMaterial();
            finalMaterial.setApplication(application);
        }

        finalMaterial.setResumeFilename(dto.fileName());
        finalMaterial.setResumeKey("users/" + user.getId() + "/applications/" + String.valueOf(applicationId) + "/resume.pdf");
        finalMaterialRepository.save(finalMaterial);

        return new FinalMaterialDto(finalMaterial.getResumeKey(), finalMaterial.getResumeFilename(), finalMaterial.getCoverLetterKey(), finalMaterial.getCoverLetterFilename());

    }

    public FinalMaterialDto saveCoverLetter(UUID applicationId, CoverLetterRequestDto dto, String email) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new EntityNotFoundException("Application nto found"));
        User user = userRepository.findByEmail(email);


        FinalMaterial finalMaterial = finalMaterialRepository.findByApplicationId(applicationId);
        if(finalMaterial == null) {
            finalMaterial = new FinalMaterial();
            finalMaterial.setApplication(application);
        }

        finalMaterial.setCoverLetterFilename(dto.fileName());
        finalMaterial.setCoverLetterKey("users/" + user.getId() + "/applications/" + String.valueOf(applicationId) + "/coverLetter.pdf");
        finalMaterialRepository.save(finalMaterial);

        return new FinalMaterialDto(finalMaterial.getResumeKey(), finalMaterial.getResumeFilename(), finalMaterial.getCoverLetterKey(), finalMaterial.getCoverLetterFilename());

    }

    public FinalMaterialDto getFinalMaterials(UUID applicationId) {
        FinalMaterial finalMaterial = finalMaterialRepository.findByApplicationId(applicationId);

        if(finalMaterial == null) {
            throw new EntityNotFoundException("Final materials not found for application " + applicationId);
        }

        return new FinalMaterialDto(finalMaterial.getResumeKey(), finalMaterial.getResumeFilename(), finalMaterial.getCoverLetterKey(), finalMaterial.getCoverLetterFilename());
    }

    @Override
    public ApplicationResponseDto findById(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId).orElseThrow(() -> new EntityNotFoundException("Application not found!"));
        return mapToResponse(application);
    }

    @Override
    public List<EditSuggestionResponse> getEditSuggestionByApplicationId(UUID applicationId) {
        List<EditSuggestion> editSuggestions = editSuggestionRepository.findAllByApplicationIdOrderByOrderIndex(applicationId);

        if(editSuggestions == null) {
            throw new EntityNotFoundException("Edit suggestions not found for application " + applicationId);
        }

        List<EditSuggestionResponse> editSuggestionResponses = new ArrayList<>();
        for(EditSuggestion editSuggestion : editSuggestions) {
            editSuggestionResponses.add(mapToResponse(editSuggestion));
        }
        return editSuggestionResponses;
    }

    @Override
    public ApplicationResponseDto updateApplication(ApplicationResponseDto dto) {

        Application application = applicationRepository.findById(dto.id()).orElseThrow(() -> new EntityNotFoundException("Application cannot be found"));
        if(!application.getCompanyName().equals(dto.companyName())) {
            application.setCompanyName(dto.companyName());
        }
        if(!application.getRoleTitle().equals(dto.roleTitle())) {
            application.setRoleTitle(dto.roleTitle());
        }
        if(!application.getLocation().equals(dto.location())) {
            application.setLocation(dto.location());
        }
        if(!application.getJobDescription().equals(dto.jobDescription())) {
            application.setJobDescription(dto.jobDescription());
        }
        if(application.getApplicationChannel() != dto.applicationChannel()) {
            application.setApplicationChannel(dto.applicationChannel());
        }
        if(application.getApplicationStatus() != dto.applicationStatus()) {
            application.setApplicationStatus(dto.applicationStatus());
        }

        application = applicationRepository.save(application);

        return mapToResponse(application);
    }

    @Override
    public String createUploadUrl(String email, UUID applicationId, String type) {
        return s3Service.createPresignedPutUrl(email, applicationId, type);
    }

    @Override
    public String createDownloadUrl(String email, UUID applicationId, String type) {
        return s3Service.createPresignedGetUrl(email, applicationId, type);
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

    private void mapToEntity(GeneratedCoverLetter generatedCoverLetter, GeneratedCoverLetterResponse generatedCoverLetterResponse, Application application) {
        generatedCoverLetter.setApplication(application);
        generatedCoverLetter.setContent(generatedCoverLetterResponse.content());
    }

}
