package com.jvprojects.jobmaster.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class StorjSnoDto {

    private String url;

    @JsonProperty("nodeID")
    private String nodeId;

    @JsonProperty("wallet")
    private String wallet;

    @JsonProperty("walletFeatures")
    private String walletFeatures;

    @JsonProperty("satellites")
    private List<Satellite> satellites;

    @JsonProperty("diskSpace")
    private DiskSpace diskSpace;

    @JsonProperty("bandwidth")
    private Bandwidth bandwidth;

    @JsonProperty("lastPinged")
    private String lastPinged;

    @JsonProperty("version")
    private String version;

    @JsonProperty("allowedVersion")
    private String allowedVersion;

    @JsonProperty("upToDate")
    private Boolean upToDate;

    @JsonProperty("startedAt")
    private String startedAt;

    @JsonProperty("configuredPort")
    private String configuredPort;

    @JsonProperty("quicStatus")
    private String quicStatus;

    @JsonProperty("lastQuicPingedAt")
    private String lastQuicPingedAt;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Satellite {

        @JsonProperty("id")
        private String id;

        @JsonProperty("url")
        private String url;

        @JsonProperty("disqualified")
        private String disqualified;

        @JsonProperty("suspended")
        private String suspended;

        @JsonProperty("vettedAt")
        private String vettedAt;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class DiskSpace {

        @JsonProperty("used")
        private Long used;

        @JsonProperty("available")
        private Long available;

        @JsonProperty("trash")
        private Long trash;

        @JsonProperty("overused")
        private Long overused;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Bandwidth {

        @JsonProperty("used")
        private Long used;

        @JsonProperty("available")
        private Long available;
    }

}
