package com.jvprojects.jobmaster.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "storj_satellites")
public class StorjSatellites extends BaseEntity{


    @OneToMany(mappedBy = "storjSatellite", cascade = CascadeType.ALL, orphanRemoval = true)
    List<StorageDaily> storageDaily;

    @OneToMany(mappedBy = "storjSatellite", cascade = CascadeType.ALL, orphanRemoval = true)
    List<BandwidthDaily> bandwidthDaily;

    @Column(name = "storage_summary")
    private Long storageSummary;

    @Column(name = "average_usage_bytes")
    private Long averageUsageBytes;

    @Column(name = "bandwidthSummary")
    private Long bandwidthSummary;

    @Column(name = "egress_summary")
    private Long egressSummary;

    @Column(name = "ingress_summary")
    private Long ingressSummary;

    @Column(name = "earliest_joined_at")
    private OffsetDateTime earliestJoinedAt;

    @OneToMany(mappedBy = "storjSatellite", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Audits> audits;

}
