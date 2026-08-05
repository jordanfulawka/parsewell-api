package com.jordanfulawka.parsewell.repository;

import com.jordanfulawka.parsewell.entity.GeneratedCoverLetter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GeneratedCoverLetterRepository extends JpaRepository<GeneratedCoverLetter, UUID> {
}
