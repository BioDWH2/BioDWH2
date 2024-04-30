package de.unibi.agbi.biodwh2.themarker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.themarker.etl.TheMarkerGraphExporter;

@JsonPropertyOrder({
        "Biomarker ID", "Biomarker Name", "Gene Name", "Synonyms", "Biomarker Type(es)", "Biomarker Class",
        "Biomarker Class2", "Function", "Gene ID", "HGNC ID", "RefSeq", "Ensembl ID", "UniProt ID", "Sequence",
        "Chromosomal location", "Precursor Accession", "Mature Accession", "TTD Target ID", "KEGG ID", "Pubchem CID",
        "Type of approval appropriate for", "Drug mechanism of action", "Age Range",
        "Disease or Use & Patient Population", "Status"
})
@GraphNodeLabel(TheMarkerGraphExporter.MARKER_LABEL)
public class Marker {
    @JsonProperty("Biomarker ID")
    @GraphProperty(GraphExporter.ID_KEY)
    public String id;
    @JsonProperty("Biomarker Name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("Gene Name")
    @GraphArrayProperty(value = "gene_name", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String geneName;
    @JsonProperty("Synonyms")
    @GraphArrayProperty(value = "synonyms", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String synonyms;
    @JsonProperty("Biomarker Type(es)")
    @GraphArrayProperty(value = "types", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String types;
    @JsonProperty("Biomarker Class")
    @GraphProperty(value = "class", emptyPlaceholder = ".")
    public String _class;
    @JsonProperty("Biomarker Class2")
    @GraphProperty(value = "class_long", emptyPlaceholder = ".")
    public String _class2;
    @JsonProperty("Function")
    @GraphProperty(value = "function", emptyPlaceholder = ".")
    public String function;
    @JsonProperty("Gene ID")
    @GraphArrayProperty(value = "gene_ids", arrayDelimiter = ";", emptyPlaceholder = ".", type = GraphArrayProperty.Type.Int)
    public String geneId;
    @JsonProperty("HGNC ID")
    @GraphArrayProperty(value = "hgnc_ids", arrayDelimiter = ";", emptyPlaceholder = ".")
    public String hgncId;
    @JsonProperty("RefSeq")
    @GraphArrayProperty(value = "refseq_ids", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String refSeq;
    @JsonProperty("Ensembl ID")
    @GraphArrayProperty(value = "ensembl_ids", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String ensemblId;
    @JsonProperty("UniProt ID")
    @GraphArrayProperty(value = "uniprot_ids", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String uniProtId;
    @JsonProperty("Sequence")
    @GraphProperty(value = "sequence", emptyPlaceholder = ".")
    public String sequence;
    @JsonProperty("Chromosomal location")
    @GraphProperty(value = "chromosomal_location", emptyPlaceholder = ".")
    public String chromosomalLocation;
    @JsonProperty("Precursor Accession")
    @GraphArrayProperty(value = "mirna_precursor_accessions", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String precursorAccession;
    @JsonProperty("Mature Accession")
    @GraphArrayProperty(value = "mirna_mature_accessions", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String matureAccession;
    @JsonProperty("TTD Target ID")
    @GraphArrayProperty(value = "ttd_target_ids", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String ttdTargetId;
    @JsonProperty("KEGG ID")
    @GraphArrayProperty(value = "kegg_ids", arrayDelimiter = ";", emptyPlaceholder = ".")
    public String keggId;
    @JsonProperty("Pubchem CID")
    @GraphArrayProperty(value = "pubchem_cids", arrayDelimiter = ";", emptyPlaceholder = ".", type = GraphArrayProperty.Type.Int)
    public String pubChemCID;
    @JsonProperty("Type of approval appropriate for")
    @GraphProperty(value = "type_of_approval_appropriate_for", ignoreEmpty = true)
    public String typeOfApprovalAppropriateFor;
    @JsonProperty("Drug mechanism of action")
    @GraphArrayProperty(value = "drug_mechanism_of_action", arrayDelimiter = "; ")
    public String drugMechanismOfAction;
    @JsonProperty("Age Range")
    @GraphArrayProperty(value = "age_range", arrayDelimiter = "; ")
    public String ageRange;
    @JsonProperty("Disease or Use & Patient Population")
    @GraphArrayProperty(value = "disease_or_use_and_patient_population", arrayDelimiter = "; ")
    public String diseaseOrUseAndPatientPopulation;
    @JsonProperty("Status")
    @GraphProperty(value = "chromosomal_location", ignoreEmpty = true)
    public String status;
}
