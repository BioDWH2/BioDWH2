package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;

import java.util.List;

@GraphNodeLabel(MarkerDBGraphExporter.CHEMICAL_LABEL)
public class Chemical {
    @GraphProperty(GraphExporter.ID_KEY)
    public Integer id;
    @JsonProperty("creation_date")
    @GraphProperty("creation_date")
    public String creationDate;
    @JsonProperty("update_date")
    @GraphProperty("update_date")
    public String updateDate;
    @GraphProperty("name")
    public String name;
    @JsonProperty("hmdb_id")
    @GraphProperty("hmdb_id")
    public String hmdbId;
    public List<Condition> conditions;
}
