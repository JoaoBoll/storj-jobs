package com.jvprojects.jobmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class EstimatedPayoutDTO {

    private String url;

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
        private BigDecimal diskSpace;

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