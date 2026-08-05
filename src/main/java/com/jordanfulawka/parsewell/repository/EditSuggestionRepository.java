package com.jordanfulawka.parsewell.repository;

import com.jordanfulawka.parsewell.entity.EditSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EditSuggestionRepository extends JpaRepository<EditSuggestion, UUID> {
}
