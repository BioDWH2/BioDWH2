package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MechanismOfAction {
    @JsonProperty("actionType")
    public String actionType;
    @JsonProperty("mechanismOfAction")
    public String mechanismOfAction;
    @JsonProperty("chemblIds")
    public String[] chemblIds;
    @JsonProperty("targetName")
    public String targetName;
    @JsonProperty("targetType")
    public String targetType;
    @JsonProperty("targets")
    public String[] targets;
    @JsonProperty("references")
    public Reference[] references;

    public static class Reference {
        @JsonProperty("source")
        public String source;
        @JsonProperty("ids")
        public String[] ids;
        @JsonProperty("urls")
        public String[] urls;
    }
}
