package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjSnoMinute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorjSnoMinuteRepository extends JpaRepository<StorjSnoMinute, Long> {
    StorjSnoMinute findByNodeId(String nodeId);
}
