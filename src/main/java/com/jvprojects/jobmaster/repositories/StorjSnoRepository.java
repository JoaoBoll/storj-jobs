package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjSno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSnoRepository extends JpaRepository<StorjSno, Long> {
    StorjSno findByNodeId(String nodeId);

    Long countByNodeIdAndCreatedAtBetween(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);

    StorjSno findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);

    StorjSno findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
}
