package com.jvprojects.jobmaster.entities;

import com.jvprojects.jobmaster.dto.StorjSatellitesDto;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "audits")
public class Audits extends BaseEntity{

    @Column(name = "audit_score")
    private BigDecimal auditScore;

    @Column(name = "suspension_score")
    private BigDecimal suspensionScore;

    @Column(name = "online_score")
    private BigDecimal onlineScore;

    @Column(name = "satellite_name")
    private String satelliteName;

    @ManyToOne
    @JoinColumn(name = "satellite_id", nullable = false)
    private StorjSatellites storjSatellite;

    public Audits() {

    }

    public Audits(StorjSatellitesDto.Audit audit) {
        this.auditScore = audit.getAuditScore();
        this.suspensionScore = audit.getSuspensionScore();
        this.onlineScore = audit.getOnlineScore();
        this.satelliteName = audit.getSatelliteName();

    }
}
