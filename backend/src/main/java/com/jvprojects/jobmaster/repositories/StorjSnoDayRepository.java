package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjSnoDay;
import com.jvprojects.jobmaster.entities.StorjSnoMinute;
import com.jvprojects.jobmaster.entities.common.StorjSno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSnoDayRepository extends JpaRepository<StorjSnoDay, Long> {
    StorjSnoDay findByNodeId(String nodeId);

    StorjSnoDay findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);

    StorjSnoDay findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
}
