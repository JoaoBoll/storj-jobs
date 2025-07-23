package com.jvprojects.jobmaster.repositories.sno;

import com.jvprojects.jobmaster.entities.StorjSnoMonth;
import com.jvprojects.jobmaster.entities.StorjSnoWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSnoMonthRepository extends JpaRepository<StorjSnoMonth, Long> {
    StorjSnoMonth findByNodeId(String nodeId);
    StorjSnoMonth findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSnoMonth findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
}
