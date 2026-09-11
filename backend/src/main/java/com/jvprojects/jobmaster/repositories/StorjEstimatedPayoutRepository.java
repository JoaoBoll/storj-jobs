package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjEstimatedPayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StorjEstimatedPayoutRepository extends JpaRepository<StorjEstimatedPayout, UUID> {
    StorjEstimatedPayout findByStorjNodeId(UUID storjNodeId);
}
