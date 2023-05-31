package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.hmdb.etl.HMDBGraphExporter;

@GraphNodeLabel(HMDBGraphExporter.PFAM_LABEL)
public class Pfam {
    @GraphProperty("name")
    public String name;
    @JsonProperty("pfam_id")
    @GraphProperty("id")
    public String pfamId;
}
