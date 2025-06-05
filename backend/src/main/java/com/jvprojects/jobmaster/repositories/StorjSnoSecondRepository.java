package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.common.StorjSno;
import com.jvprojects.jobmaster.entities.StorjSnoSecond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSnoSecondRepository extends JpaRepository<StorjSnoSecond, Long> {
    StorjSnoSecond findByNodeId(String nodeId);

    Long countByNodeIdAndCreatedAtBetween(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);

    StorjSnoSecond findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);

    StorjSnoSecond findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
}
