package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Indication {
    @JsonProperty("id")
    public String id;
    @JsonProperty("approvedIndications")
    public String[] approvedIndications;
    @JsonProperty("indicationCount")
    public Integer indicationCount;
    @JsonProperty("indications")
    public Entry[] indications;

    public static class Entry {
        @JsonProperty("disease")
        public String disease;
        @JsonProperty("efoName")
        public String efoName;
        @JsonProperty("maxPhaseForIndication")
        public Integer maxPhaseForIndication;
        @JsonProperty("references")
        public Reference[] references;
    }

    public static class Reference {
        @JsonProperty("source")
        public String source;
        @JsonProperty("ids")
        public String[] ids;
    }
}
