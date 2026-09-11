package com.jvprojects.jobmaster.repositories.sno;

import com.jvprojects.jobmaster.entities.StorjSno15m;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSno15mRepository extends JpaRepository<StorjSno15m, Long> {
    StorjSno15m findByNodeId(String nodeId);
    StorjSno15m findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSno15m findFirstByNodeIdOrderByCreatedAtAsc(String nodeId);
    StorjSno15m findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSno15m findFirstByNodeIdOrderByCreatedAtDesc(String nodeId);
}
