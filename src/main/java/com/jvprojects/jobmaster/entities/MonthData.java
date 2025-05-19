package com.jvprojects.jobmaster.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "month_data")
public class MonthData extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "storj_estimated_payout", nullable = false)
    private StorjEstimatedPayout storjEstimatedPayout;

    @Column(name = "egress_bandwidth")
    private Long egressBandwidth;

    @Column(name = "egress_bandwidth_payout", precision = 10, scale = 2)
    private BigDecimal egressBandwidthPayout;

    @Column(name = "egress_repair_audit")
    private Long egressRepairAudit;

    @Column(name = "egress_repair_audit_payout", precision = 10, scale = 2)
    private BigDecimal egressRepairAuditPayout;

    @Column(name = "disk_space", precision = 20, scale = 5)
    private BigDecimal diskSpace;

    @Column(name = "disk_space_payout", precision = 10, scale = 2)
    private BigDecimal diskSpacePayout;

    @Column(name = "held_rate", precision = 5, scale = 2)
    private BigDecimal heldRate;

    @Column(name = "payout", precision = 10, scale = 2)
    private BigDecimal payout;

    @Column(name = "held", precision = 10, scale = 2)
    private BigDecimal held;
}
