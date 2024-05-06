package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.npcdr.etl.NPCDRGraphExporter;

@JsonPropertyOrder({
        "ProteinID", "Protein_Name", "Synonyms", "Gene_Name", "Uniprot", "UniprotAC", "Gene_ID", "EC_number",
        "TC_number", "Pfam", "Sequence", "Function", "TTDID", "keggid", "Intede", "Varidt", "miRBase"
})
@GraphNodeLabel(NPCDRGraphExporter.MOLECULE_LABEL)
public class Molecule {
    @JsonProperty("ProteinID")
    @GraphProperty(GraphExporter.ID_KEY)
    public String proteinId;
    @JsonProperty("Protein_Name")
    @GraphProperty("name")
    public String proteinName;
    @JsonProperty("Synonyms")
    @GraphArrayProperty(value = "synonyms", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String synonyms;
    @JsonProperty("Gene_Name")
    @GraphArrayProperty(value = "gene_names", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String geneName;
    @JsonProperty("Uniprot")
    @GraphArrayProperty(value = "uniprot_ids", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String uniprot;
    @JsonProperty("UniprotAC")
    @GraphArrayProperty(value = "uniprot_accessions", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String uniprotAccession;
    @JsonProperty("Gene_ID")
    public String geneId;
    @JsonProperty("EC_number")
    @GraphArrayProperty(value = "ec_numbers", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String ecNumber;
    @JsonProperty("TC_number")
    @GraphArrayProperty(value = "tc_numbers", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String tcNumber;
    @JsonProperty("Pfam")
    @GraphArrayProperty(value = "pfam_ids", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String pfam;
    @JsonProperty("Sequence")
    @GraphProperty(value = "sequence", emptyPlaceholder = ".")
    public String sequence;
    @JsonProperty("Function")
    @GraphProperty(value = "function", emptyPlaceholder = ".")
    public String function;
    @JsonProperty("TTDID")
    @GraphProperty(value = "ttd_id", emptyPlaceholder = ".")
    public String ttdId;
    @JsonProperty("keggid")
    public String keggId;
    @JsonProperty("Intede")
    @GraphProperty(value = "intede", emptyPlaceholder = ".")
    public String intede;
    @JsonProperty("Varidt")
    @GraphProperty(value = "varidt", emptyPlaceholder = ".")
    public String varidt;
    @JsonProperty("miRBase")
    @GraphProperty(value = "mirbase", emptyPlaceholder = ".")
    public String mirBase;
}
