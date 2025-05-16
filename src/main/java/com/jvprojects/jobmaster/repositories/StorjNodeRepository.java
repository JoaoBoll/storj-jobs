package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorjNodeRepository extends JpaRepository<StorjNode, Long> {
    StorjNode findByNodeId(String nodeId);
}
