package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaselineExpression {
    @JsonProperty("id")
    public String id;
    @JsonProperty("tissues")
    public Tissue[] tissues;

    public static class Tissue {
        @JsonProperty("anatomical_systems")
        public String[] anatomicalSystems;
        @JsonProperty("efo_code")
        public String efoCode;
        @JsonProperty("label")
        public String label;
        @JsonProperty("organs")
        public String[] organs;
        @JsonProperty("protein")
        public Protein protein;
        @JsonProperty("rna")
        public RNA rna;
    }

    public static class Protein {
        @JsonProperty("cell_type")
        public CellType[] cellTypes;
        @JsonProperty("level")
        public Integer level;
        @JsonProperty("reliability")
        public Boolean reliability;
    }

    public static class CellType {
        @JsonProperty("level")
        public Integer level;
        @JsonProperty("name")
        public String name;
        @JsonProperty("reliability")
        public Boolean reliability;
    }

    public static class RNA {
        @JsonProperty("level")
        public Integer level;
        @JsonProperty("unit")
        public String unit;
        @JsonProperty("value")
        public Double value;
        @JsonProperty("zscore")
        public Integer zScore;
    }
}
