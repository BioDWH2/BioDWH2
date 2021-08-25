package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DrugWarning {
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("meddraSocCode")
    public Integer meddraSocCode;
    @JsonProperty("year")
    public Integer year;
    @JsonProperty("toxicityClass")
    public String toxicityClass;
    @JsonProperty("chemblIds")
    public String[] chemblIds;
    @JsonProperty("country")
    public String country;
    @JsonProperty("description")
    public String description;
    @JsonProperty("warningType")
    public String warningType;
    @JsonProperty("references")
    public Reference[] references;

    public static class Reference {
        @JsonProperty("ref_id")
        public String refId;
        @JsonProperty("ref_type")
        public String refType;
        @JsonProperty("ref_url")
        public String refUrl;
    }
}
