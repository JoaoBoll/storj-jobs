package com.jvprojects.jobmaster.repositories;

import com.jvprojects.jobmaster.entities.StorjSatellites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface StorjSatellitesRepository extends JpaRepository<StorjSatellites, Long> {
    StorjSatellites findByStorjNodeId(UUID storjNodeId);

    List<StorjSatellites> findByCreatedAtBetween(OffsetDateTime startDate, OffsetDateTime endDate);

}
