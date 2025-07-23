package com.jvprojects.jobmaster.repositories.sno;

import com.jvprojects.jobmaster.entities.StorjSnoMinute;
import com.jvprojects.jobmaster.entities.StorjSnoWeek;
import com.jvprojects.jobmaster.entities.common.StorjSno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSnoWeekRepository extends JpaRepository<StorjSnoWeek, Long> {
    StorjSnoWeek findByNodeId(String nodeId);
    StorjSnoWeek findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSnoWeek findFirstByNodeIdOrderByCreatedAtAsc(String nodeId);
    StorjSnoWeek findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSnoWeek findFirstByNodeIdOrderByCreatedAtDesc(String nodeId);

}
