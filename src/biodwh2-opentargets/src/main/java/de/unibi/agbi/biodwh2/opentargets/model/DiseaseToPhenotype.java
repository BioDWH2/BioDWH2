package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DiseaseToPhenotype {
    @JsonProperty("disease")
    public String disease;
    @JsonProperty("phenotype")
    public String phenotype;
    @JsonProperty("evidence")
    public Evidence[] evidences;

    public static class Evidence {
        @JsonProperty("aspect")
        public String aspect;
        @JsonProperty("bioCuration")
        public String bioCuration;
        @JsonProperty("diseaseFromSourceId")
        public String diseaseFromSourceId;
        @JsonProperty("diseaseFromSource")
        public String diseaseFromSource;
        @JsonProperty("diseaseName")
        public String diseaseName;
        @JsonProperty("evidenceType")
        public String evidenceType;
        @JsonProperty("frequency")
        public String frequency;
        @JsonProperty("modifiers")
        public String[] modifiers;
        @JsonProperty("onset")
        public String[] onset;
        @JsonProperty("qualifier")
        public String qualifier;
        @JsonProperty("qualifierNot")
        public Boolean qualifierNot;
        @JsonProperty("references")
        public String[] references;
        @JsonProperty("sex")
        public String sex;
        @JsonProperty("resource")
        public String resource;

    }
}
