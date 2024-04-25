package de.unibi.agbi.biodwh2.themarker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
    @GraphProperty("id")
    public String id;
    @JsonProperty("Biomarker Name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("Gene Name")
    public String geneName;
    @JsonProperty("Synonyms")
    public String synonyms;
    @JsonProperty("Biomarker Type(es)")
    public String types;
    @JsonProperty("Biomarker Class")
    public String _class;
    @JsonProperty("Biomarker Class2")
    public String _class2;
    @JsonProperty("Function")
    public String function;
    @JsonProperty("Gene ID")
    public String geneId;
    @JsonProperty("HGNC ID")
    public String hgncId;
    @JsonProperty("RefSeq")
    public String refSeq;
    @JsonProperty("Ensembl ID")
    public String ensemblId;
    @JsonProperty("UniProt ID")
    public String uniProtId;
    @JsonProperty("Sequence")
    public String sequence;
    @JsonProperty("Chromosomal location")
    public String chromosomalLocation;
    @JsonProperty("Precursor Accession")
    public String precursorAccession;
    @JsonProperty("Mature Accession")
    public String matureAccession;
    @JsonProperty("TTD Target ID")
    public String ttdTargetId;
    @JsonProperty("KEGG ID")
    public String keggId;
    @JsonProperty("Pubchem CID")
    public String pubChemCID;
    @JsonProperty("Type of approval appropriate for")
    public String typeOfApprovalAppropriateFor;
    @JsonProperty("Drug mechanism of action")
    public String drugMechanismOfAction;
    @JsonProperty("Age Range")
    public String ageRange;
    @JsonProperty("Disease or Use & Patient Population")
    public String diseaseOrUseAndPatientPopulation;
    @JsonProperty("Status")
    public String status;
}
