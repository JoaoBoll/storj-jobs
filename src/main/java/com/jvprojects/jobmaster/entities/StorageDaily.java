package com.jvprojects.jobmaster.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "storage_daily")
public class StorageDaily extends BaseEntity{

    @Column(name = "at_rest_total")
    private Long atRestTotal;

    @Column(name = "at_rest_total_bytes")
    private Long atRestTotalBytes;

    @Column(name = "interval_start")
    private OffsetDateTime intervalStart;

    @ManyToOne
    @JoinColumn(name = "satellite_id", nullable = false)
    private StorjSatellites storjSatellite;

}
