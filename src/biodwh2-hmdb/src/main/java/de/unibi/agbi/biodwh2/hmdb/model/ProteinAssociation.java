package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.hmdb.etl.HMDBGraphExporter;

@GraphNodeLabel(HMDBGraphExporter.PROTEIN_LABEL)
public class ProteinAssociation {
    @JsonProperty("protein_accession")
    @GraphProperty("accession")
    public String proteinAccession;
    @GraphProperty("name")
    public String name;
    @JsonProperty("uniprot_id")
    @GraphProperty("uniprot_id")
    public String uniprotId;
    @JsonProperty("gene_name")
    @GraphProperty("gene_name")
    public String geneName;
    @JsonProperty("protein_type")
    @GraphProperty("protein_type")
    public String proteinType;
}
