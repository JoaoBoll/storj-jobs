package com.jvprojects.jobmaster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StorjSatellitesDto {

    private String ulr;

    @JsonProperty("storageDaily")
    private List<StorageDaily> storageDaily;

    @JsonProperty("bandwidthDaily")
    private List<BandwidthDaily> bandwidthDaily;

    @JsonProperty("storageSummary")
    private BigDecimal storageSummary;

    @JsonProperty("averageUsageBytes")
    private BigDecimal averageUsageBytes;

    @JsonProperty("bandwidthSummary")
    private BigDecimal bandwidthSummary;

    @JsonProperty("egressSummary")
    private BigDecimal egressSummary;

    @JsonProperty("ingressSummary")
    private BigDecimal ingressSummary;

    @JsonProperty("earliestJoinedAt")
    private String earliestJoinedAt;

    @JsonProperty("audits")
    private List<Audit> audits;

    @Data
    public static class StorageDaily {
        @JsonProperty("atRestTotal")
        private Long atRestTotal;

        @JsonProperty("atRestTotalBytes")
        private Long atRestTotalBytes;

        @JsonProperty("intervalStart")
        private String intervalStart;
    }

    @Data
    public static class BandwidthDaily {
        @JsonProperty("egress")
        private Egress egress;

        @JsonProperty("ingress")
        private Ingress ingress;

        @JsonProperty("delete")
        private Integer delete;

        @JsonProperty("intervalStart")
        private String intervalStart;
    }

    @Data
    public static class Egress {
        @JsonProperty("repair")
        private Long repair;

        @JsonProperty("audit")
        private Long audit;

        @JsonProperty("usage")
        private Long usage;
    }

    @Data
    public static class Ingress {
        @JsonProperty("repair")
        private Long repair;

        @JsonProperty("usage")
        private Long usage;
    }

    @Data
    public static class Audit {
        @JsonProperty("auditScore")
        private BigDecimal auditScore;

        @JsonProperty("suspensionScore")
        private BigDecimal suspensionScore;

        @JsonProperty("onlineScore")
        private BigDecimal onlineScore;

        @JsonProperty("satelliteName")
        private String satelliteName;
    }


}
