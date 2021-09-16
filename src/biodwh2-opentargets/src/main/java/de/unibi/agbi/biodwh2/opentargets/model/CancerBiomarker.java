package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CancerBiomarker {
    @JsonProperty("id")
    public String id;
    @JsonProperty("drugName")
    public String drugName;
    @JsonProperty("target")
    public String target;
    @JsonProperty("disease")
    public String disease;
    @JsonProperty("evidenceLevel")
    public String evidenceLevel;
    @JsonProperty("associationType")
    public String associationType;
    @JsonProperty("sourcesPubmed")
    public Integer[] sourcesPubmed;
    @JsonProperty("sourcesOther")
    public Source[] sourcesOther;

    public static class Source {
        @JsonProperty("description")
        public String description;
        @JsonProperty("link")
        public String link;
        @JsonProperty("name")
        public String name;
    }
}
