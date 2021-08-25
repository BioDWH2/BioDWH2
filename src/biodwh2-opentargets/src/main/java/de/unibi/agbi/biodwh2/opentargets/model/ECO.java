package de.unibi.agbi.biodwh2.opentargets.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ECO {
    @JsonProperty("id")
    public String id;
    @JsonProperty("code")
    public String code;
    @JsonProperty("label")
    public String label;
    @JsonProperty("path_labels")
    public String[][] pathLabels;
    @JsonProperty("path_codes")
    public String[][] pathCodes;
    @JsonProperty("path_codes")
    public PathElement[][] path;

    public static class PathElement {
        @JsonProperty("label")
        public String label;
        @JsonProperty("uri")
        public String uri;
    }
}
