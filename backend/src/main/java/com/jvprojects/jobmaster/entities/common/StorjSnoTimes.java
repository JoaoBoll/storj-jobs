package com.jvprojects.jobmaster.entities.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.math.BigDecimal;

@Data
@MappedSuperclass
public class StorjSnoTimes extends StorjSno {

    @Column(name = "total_used_bandwidth")
    private Long totalUsedBandwidth;

    @Column(name = "total_consume_bandwidth_per_second")
    private Long totalConsumeBandwidthPerSecond;

    @Column(name = "ingress_total")
    private Long ingressTotal;

    @Column(name = "egress_total")
    private Long egressTotal;

    @Column(name = "uptime_score_sum")
    private Double uptimeScoreSum;

    @Column(name = "uptime_score_count")
    private Integer uptimeScoreCount;

    @Column(name = "estimated_payout", precision = 10, scale = 2)
    private BigDecimal estimatedPayout;

    @Column(name = "current_month_payout", precision = 10, scale = 2)
    private BigDecimal currentMonthPayout;

}
