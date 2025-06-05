package com.jvprojects.jobmaster.entities.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class StorjSnoTimes extends StorjSno {

    @Column(name = "total_used_bandwidth")
    private Long totalUsedBandwidth;

    @Column(name = "total_consume_bandwidth_per_second")
    private Long totalConsumeBandwidthPerSecond;

}
