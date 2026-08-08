package com.jordanfulawka.parsewell.repository;

import com.jordanfulawka.parsewell.entity.BaseResume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BaseResumeRepository extends JpaRepository<BaseResume, UUID> {
    BaseResume findByUser_Email(String email);
}
