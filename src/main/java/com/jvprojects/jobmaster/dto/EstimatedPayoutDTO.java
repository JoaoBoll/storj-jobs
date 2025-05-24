package com.jvprojects.jobmaster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EstimatedPayoutDTO {
    @JsonProperty("currentMonth")
    private MonthData currentMonth;

    @JsonProperty("previousMonth")
    private MonthData previousMonth;

    @JsonProperty("currentMonthExpectations")
    private Double currentMonthExpectations;

    @Data
    public static class MonthData {
        @JsonProperty("egressBandwidth")
        private Long egressBandwidth;

        @JsonProperty("egressBandwidthPayout")
        private BigDecimal egressBandwidthPayout;

        @JsonProperty("egressRepairAudit")
        private Long egressRepairAudit;

        @JsonProperty("egressRepairAuditPayout")
        private BigDecimal egressRepairAuditPayout;

        @JsonProperty("diskSpace")
        private Long diskSpace;

        @JsonProperty("diskSpacePayout")
        private BigDecimal diskSpacePayout;

        @JsonProperty("heldRate")
        private Double heldRate;

        @JsonProperty("payout")
        private BigDecimal payout;

        @JsonProperty("held")
        private BigDecimal held;

    }
}