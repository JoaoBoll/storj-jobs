package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StorjNodeRepository extends JpaRepository<StorjNode, UUID> {
    StorjNode findByNodeId(String nodeId);
    StorjNode findByUrl(String url);

    List<StorjNode> findAllByEnabledIsTrue();
}
