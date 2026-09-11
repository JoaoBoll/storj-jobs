package com.jvprojects.jobmaster.repositories.sno;

import com.jvprojects.jobmaster.entities.StorjSno5m;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSno5mRepository extends JpaRepository<StorjSno5m, Long> {
    StorjSno5m findByNodeId(String nodeId);
    StorjSno5m findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSno5m findFirstByNodeIdOrderByCreatedAtAsc(String nodeId);
    StorjSno5m findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSno5m findFirstByNodeIdOrderByCreatedAtDesc(String nodeId);
}
