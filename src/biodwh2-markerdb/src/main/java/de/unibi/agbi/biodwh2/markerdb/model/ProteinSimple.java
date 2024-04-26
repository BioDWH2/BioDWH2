package de.unibi.agbi.biodwh2.markerdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.markerdb.etl.MarkerDBGraphExporter;

@GraphNodeLabel(MarkerDBGraphExporter.PROTEIN_LABEL)
public class ProteinSimple {
    @JsonProperty("biomarker_type")
    public String biomarkerType;
    @GraphNumberProperty(GraphExporter.ID_KEY)
    public String id;
    @GraphProperty("name")
    public String name;
    @JsonProperty("gene_name")
    @GraphProperty("gene_name")
    public String geneName;
    @JsonProperty("uniprot_id")
    @GraphProperty("uniprot_id")
    public String uniprotId;
    public String conditions;
    @JsonProperty("indication_types")
    public String indicationTypes;
    public String concentration;
    public String age;
    public String sex;
    public String biofluid;
    public String citation;
}
