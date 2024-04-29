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
    @GraphProperty(value = "creation_date", ignoreEmpty = true)
    public String creationDate;
    @JsonProperty("update_date")
    @GraphProperty(value = "update_date", ignoreEmpty = true)
    public String updateDate;
    @GraphProperty("name")
    public String name;
    @JsonProperty("hmdb_id")
    @GraphProperty(value = "hmdb_id", ignoreEmpty = true)
    public String hmdbId;
    public List<Condition> conditions;
}
