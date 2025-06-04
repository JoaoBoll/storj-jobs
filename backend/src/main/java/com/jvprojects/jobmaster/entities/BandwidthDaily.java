package com.jvprojects.jobmaster.entities;

import com.jvprojects.jobmaster.dto.StorjSatellitesDto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "bandwidth_daily")
public class BandwidthDaily extends BaseEntity{

    @Column(name = "egress_repair")
    private Long egressRepair;

    @Column(name = "egress_audit")
    private Long egressAudit;

    @Column(name = "egress_usage")
    private Long egressUsage;

    @Column(name = "ingress_repair")
    private Long ingressRepair;

    @Column(name = "ingress_usage")
    private Long ingressUsage;

    @Column(name = "delete")
    private Integer delete;

    @Column(name = "interval_start")
    private OffsetDateTime intervalStart;

    @ManyToOne
    @JoinColumn(name = "satellite_id", nullable = false)
    private StorjSatellites storjSatellite;

    public BandwidthDaily() {

    }

    public BandwidthDaily(StorjSatellitesDto.BandwidthDaily bandwidthDaily) {
        this.egressRepair = bandwidthDaily.getEgress().getRepair();
        this.egressAudit = bandwidthDaily.getEgress().getAudit();
        this.egressUsage = bandwidthDaily.getEgress().getUsage();
        this.ingressRepair = bandwidthDaily.getIngress().getRepair();
        this.ingressUsage = bandwidthDaily.getIngress().getUsage();
        this.delete = bandwidthDaily.getDelete();
        this.intervalStart = OffsetDateTime.parse(bandwidthDaily.getIntervalStart());
    }

}
