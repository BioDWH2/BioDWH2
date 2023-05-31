package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.hmdb.etl.HMDBGraphExporter;

@GraphNodeLabel(HMDBGraphExporter.GO_CLASS_LABEL)
public class GOClass {
    @GraphProperty("category")
    public String category;
    @GraphProperty("description")
    public String description;
    @JsonProperty("go_id")
    @GraphProperty("id")
    public String goId;
}
