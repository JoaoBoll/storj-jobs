package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjSno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorjSnoRepository extends JpaRepository<StorjSno, Long> {
    StorjSno findByNodeId(String nodeId);
}
