package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.hmdb.etl.HMDBGraphExporter;

@GraphNodeLabel(HMDBGraphExporter.PROTEIN_LABEL)
public class ProteinAssociation {
    @JsonProperty("protein_accession")
    public String proteinAccession;
    public String name;
    @JsonProperty("uniprot_id")
    public String uniprotId;
    @JsonProperty("gene_name")
    public String geneName;
    @JsonProperty("protein_type")
    public String proteinType;
}
