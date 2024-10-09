package de.unibi.agbi.biodwh2.ptmd.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.ptmd.etl.PTMDGraphExporter;

@JsonPropertyOrder({
        "Uniprot ID", "Accessions", "Genbank Nucleotide ID", "Genbank Protein ID", "Functional Description", "Keywords",
        "Protein Sequence"
})
@GraphNodeLabel(PTMDGraphExporter.PROTEIN_LABEL)
public class Protein {
    @JsonProperty("Uniprot ID")
    @GraphProperty(value = "uniprot_id")
    public String uniprotId;
    @JsonProperty("Accessions")
    @GraphArrayProperty(value = "accessions", arrayDelimiter = ";")
    public String accessions;
    @JsonProperty("Genbank Nucleotide ID")
    @GraphArrayProperty("genbank_nucleotide_id")
    public String genbank_nucleotide_id;
    @JsonProperty("Genbank Protein ID")
    @GraphArrayProperty("genbank_protein_id")
    public String genbankProteinId;
    @JsonProperty("Functional Description")
    @GraphProperty("functional_description")
    public String functionalDescription;
    @JsonProperty("Keywords")
    @GraphArrayProperty("keywords")
    public String keywords;
    @JsonProperty("Protein Sequence")
    @GraphProperty("protein_sequence")
    public String proteinSequence;
}
