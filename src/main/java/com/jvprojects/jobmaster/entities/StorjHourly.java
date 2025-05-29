package com.jvprojects.jobmaster.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
//@Entity
//@Table(name = "storj_hour")
public class StorjHourly extends BaseEntity{

    @OneToOne
    @JoinColumn(name = "storj_node_id", nullable = false)
    private StorjNode storjNode;

    @Column(name = "storage_summary")
    private BigDecimal storageSummary;

    @Column(name = "average_usage_bytes")
    private BigDecimal averageUsageBytes;

    @Column(name = "bandwidthSummary")
    private BigDecimal bandwidthSummary;

    @Column(name = "egress_summary")
    private BigDecimal egressSummary;

    @Column(name = "ingress_summary")
    private BigDecimal ingressSummary;

    @Column(name = "earliest_joined_at")
    private OffsetDateTime earliestJoinedAt;

    @OneToMany(mappedBy = "storjSatellite", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Audits> audits;

}
