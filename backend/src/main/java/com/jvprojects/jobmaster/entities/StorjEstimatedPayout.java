package com.jvprojects.jobmaster.entities;

import com.jvprojects.jobmaster.entities.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "storj_estimated_payout")
public class StorjEstimatedPayout extends BaseEntity {

    @OneToOne(mappedBy = "storjEstimatedPayout")
    @JoinColumn(name = "current_month_id", referencedColumnName = "id")
    private MonthData currentMonth;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "previous_month_id", referencedColumnName = "id")
    private MonthData previousMonth;

    @Column(name = "current_month_expectations", precision = 10, scale = 2)
    private BigDecimal currentMonthExpectations;

}
