package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjSnoMinutes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorjSnoMinutesRepository extends JpaRepository<StorjSnoMinutes, Long> {
    StorjSnoMinutes findByNodeId(String nodeId);
}
