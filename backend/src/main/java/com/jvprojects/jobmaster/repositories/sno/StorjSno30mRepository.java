package com.jvprojects.jobmaster.repositories.sno;

import com.jvprojects.jobmaster.entities.StorjSno30m;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSno30mRepository extends JpaRepository<StorjSno30m, Long> {
    StorjSno30m findByNodeId(String nodeId);
    StorjSno30m findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSno30m findFirstByNodeIdOrderByCreatedAtAsc(String nodeId);
    StorjSno30m findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSno30m findFirstByNodeIdOrderByCreatedAtDesc(String nodeId);
}
