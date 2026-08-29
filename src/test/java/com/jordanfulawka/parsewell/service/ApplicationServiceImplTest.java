package com.jordanfulawka.parsewell.service;

import com.jordanfulawka.parsewell.dto.applications.ApplicationResponseDto;
import com.jordanfulawka.parsewell.entity.Application;
import com.jordanfulawka.parsewell.entity.BaseResume;
import com.jordanfulawka.parsewell.entity.User;
import com.jordanfulawka.parsewell.entity.enums.ApplicationChannel;
import com.jordanfulawka.parsewell.entity.enums.ApplicationStatus;
import com.jordanfulawka.parsewell.repository.*;
import com.jordanfulawka.parsewell.service.ai.ClaudeService;
import com.jordanfulawka.parsewell.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private UserRepository userRepository;
    @Mock private BaseResumeRepository baseResumeRepository;
    @Mock private ClaudeService claudeService;
    @Mock private EditSuggestionRepository editSuggestionRepository;
    @Mock private GeneratedCoverLetterRepository generatedCoverLetterRepository;
    @Mock private FinalMaterialRepository finalMaterialRepository;
    @Mock private S3Service s3Service;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private Application application;
    private UUID applicationId;

    @BeforeEach
    void setUp() {
        applicationId = UUID.randomUUID();

        User user = new User();
        user.setId(UUID.randomUUID());

        BaseResume baseResume = new BaseResume();
        baseResume.setId(UUID.randomUUID());

        application = new Application(
                user, baseResume, "Acme Corp", "Backend Engineer", "Remote",
                "https://acme.example/job/1", "Job description text",
                ApplicationChannel.COLD_APPLICATION, ApplicationStatus.DRAFT, "some notes"
        );
        application.setId(applicationId);
    }

    @Test
    void findById_returnsMappedResponse_whenApplicationExists() {
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        ApplicationResponseDto result = applicationService.findById(applicationId);

        assertThat(result.id()).isEqualTo(applicationId);
        assertThat(result.companyName()).isEqualTo("Acme Corp");
        assertThat(result.roleTitle()).isEqualTo("Backend Engineer");
    }

    @Test
    void findById_throwsEntityNotFoundException_whenApplicationMissing() {
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.findById(applicationId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void deleteApplication_deletesApplication_whenItExists() {
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        applicationService.deleteApplication(applicationId);

        verify(applicationRepository).delete(application);
    }

    @Test
    void deleteApplication_throwsEntityNotFoundException_whenApplicationMissing() {
        when(applicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.deleteApplication(applicationId))
                .isInstanceOf(EntityNotFoundException.class);

        verify(applicationRepository, never()).delete(any());
    }
}
