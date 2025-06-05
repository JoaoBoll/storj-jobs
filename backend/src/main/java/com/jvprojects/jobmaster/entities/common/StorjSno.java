package com.jvprojects.jobmaster.entities.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class StorjSno extends BaseEntity {

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
