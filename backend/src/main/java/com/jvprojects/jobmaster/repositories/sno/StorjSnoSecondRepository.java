package com.jvprojects.jobmaster.repositories.sno;

import com.jvprojects.jobmaster.entities.common.StorjSno;
import com.jvprojects.jobmaster.entities.StorjSnoSecond;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface StorjSnoSecondRepository extends JpaRepository<StorjSnoSecond, Long> {
    java.util.List<StorjSnoSecond> findAllByOrderByCreatedAtDesc();

    @org.springframework.data.jpa.repository.Query("SELECT s FROM StorjSnoSecond s WHERE s.createdAt BETWEEN ?1 AND ?2 ORDER BY s.createdAt DESC")
    java.util.List<StorjSnoSecond> findByCreatedAtBetweenOrderByCreatedAtDesc(OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSnoSecond findByNodeId(String nodeId);
    Long countByNodeIdAndCreatedAtBetween(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSnoSecond findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtAsc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSnoSecond findFirstByNodeIdOrderByCreatedAtAsc(String nodeId);
    StorjSnoSecond findFirstByNodeIdAndCreatedAtBetweenOrderByCreatedAtDesc(String nodeId, OffsetDateTime startDate, OffsetDateTime endDate);
    StorjSnoSecond findFirstByNodeIdOrderByCreatedAtDesc(String nodeId);

}
