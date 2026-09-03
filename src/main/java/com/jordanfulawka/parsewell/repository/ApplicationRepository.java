package com.jordanfulawka.parsewell.repository;

import com.jordanfulawka.parsewell.dto.applications.ApplicationDTO;
import com.jordanfulawka.parsewell.entity.Application;
import com.jordanfulawka.parsewell.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    @Query("SELECT new com.jordanfulawka.parsewell.dto.applications.ApplicationDTO" +
            "(id, companyName, roleTitle, location, jobURL, applicationChannel, applicationStatus, createdAt, updatedAt) " +
            "FROM Application a " +
            "WHERE user.id = :id " +
            "ORDER BY updatedAt DESC")
    List<ApplicationDTO> findAllByUserIdOrderByUpdatedAtDesc(@Param("id") UUID id);

    @Query("""
        SELECT
            SUM(CASE WHEN a.applicationStatus <> :draft THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.createdAt >= :since THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.applicationStatus = :applied THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.applicationStatus = :heardBack THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.applicationStatus = :rejected THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.applicationStatus = :ghosted THEN 1 ELSE 0 END),
            SUM(CASE WHEN a.applicationStatus NOT IN (:applied, :heardBack, :rejected, :ghosted) THEN 1 ELSE 0 END)
            FROM Application a
            WHERE a.user.id = :userId""")
    List<Object[]> getInsightsCounts(@Param("userId") UUID userId,
                                @Param("since") LocalDateTime since,
                                @Param("draft") ApplicationStatus draft,
                                @Param("applied") ApplicationStatus applied,
                                @Param("heardBack") ApplicationStatus heardBack,
                                @Param("rejected") ApplicationStatus rejected,
                                @Param("ghosted") ApplicationStatus ghosted);
}
