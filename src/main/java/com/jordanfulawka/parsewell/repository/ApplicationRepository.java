package com.jordanfulawka.parsewell.repository;

import com.jordanfulawka.parsewell.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findAllByUserIdOrderByUpdatedAtDesc(UUID id);
}
