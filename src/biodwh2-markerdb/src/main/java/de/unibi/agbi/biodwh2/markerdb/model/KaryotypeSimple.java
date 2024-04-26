package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;

@GraphNodeLabel(MarkerDBGraphExporter.KARYOTYPE_LABEL)
public class KaryotypeSimple {
    @JsonProperty("biomarker_type")
    public String biomarkerType;
    @GraphNumberProperty(GraphExporter.ID_KEY)
    public String id;
    @GraphProperty("karyotype")
    public String karyotype;
    @GraphProperty("description")
    public String description;
    @JsonProperty("indication_types")
    public String indication_types;
    public String conditions;
}
