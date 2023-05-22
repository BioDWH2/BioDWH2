package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pathway {
    public String name;
    @JsonProperty("smpdb_id")
    public String smpdbId;
    @JsonProperty("kegg_map_id")
    public String keggMapId;
}
