package de.unibi.agbi.biodwh2.iptmnet.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.iptmnet.etl.IPTMNetGraphExporter;

@JsonPropertyOrder({
        "UniProtAC", "UniProtID", "protein_name", "genename", "organism", "PRO_ID", "UniProtKB-SwissProt_or_TrEmbl"
})
@GraphNodeLabel(IPTMNetGraphExporter.PROTEIN_LABEL)
public class Protein {
    @JsonProperty("UniProtAC")
    @GraphProperty("uniprot_accession")
    public String uniProtAccession;
    @JsonProperty("UniProtID")
    @GraphProperty("uniprot_id")
    public String uniProtId;
    @JsonProperty("protein_name")
    public String name;
    @JsonProperty("genename")
    public String geneName;
    @JsonProperty("organism")
    public String organism;
    @JsonProperty("PRO_ID")
    @GraphProperty("pro_id")
    public String proId;
    @JsonProperty("UniProtKB-SwissProt_or_TrEmbl")
    @GraphBooleanProperty(value = "reviewed", truthValue = "SP")
    public String swissProtOrTrembl;
}
