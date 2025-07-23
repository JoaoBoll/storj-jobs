package com.jvprojects.jobmaster.repositories.sno;

import com.jvprojects.jobmaster.entities.StorjSnoHour;
import com.jvprojects.jobmaster.entities.StorjSnoMinute;
import com.jvprojects.jobmaster.entities.common.StorjSno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSnoHourRepository extends JpaRepository<StorjSnoHour, Long> {
    StorjSnoHour findByNodeId(String nodeId);
    StorjSnoHour findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSnoHour findFirstByNodeIdOrderByCreatedAtAsc(String nodeId);
    StorjSnoHour findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSnoHour findFirstByNodeIdOrderByCreatedAtDesc(String nodeId);
}
