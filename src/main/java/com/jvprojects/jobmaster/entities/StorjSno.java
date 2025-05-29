package com.jvprojects.jobmaster.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "storj_sno")
public class StorjSno extends BaseEntity{

    @Column(name = "node_id")
    private String nodeId;

    @Column(name = "used_disk_space")
    private Long usedDiskSpace;

    @Column(name = "trash_disk_space")
    private Long trashDiskSpace;

    @Column(name = "used_bandwidth")
    private Long usedBandwidth;

    @Column(name = "overused_disk_space")
    private Long overusedDiskSpace;

}
