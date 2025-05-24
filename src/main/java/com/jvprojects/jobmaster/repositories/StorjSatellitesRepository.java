package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjSatellites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StorjSatellitesRepository extends JpaRepository<StorjSatellites, Long> {
    StorjSatellites findByStorjNodeId(UUID storjNodeId);
}
