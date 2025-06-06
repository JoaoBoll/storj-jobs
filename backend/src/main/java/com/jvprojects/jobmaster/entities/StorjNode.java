package com.jvprojects.jobmaster.entities;

import com.jvprojects.jobmaster.entities.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "storj_node")
public class StorjNode extends BaseEntity {

    @Column
    private String nodeId;

    @Column
    private String url;

    @OneToOne(mappedBy = "storjNode")
    private StorjSatellites storjSatellites;

    @Column
    private Boolean enabled;

    @Column
    private Long availableDiskSpace;


}

