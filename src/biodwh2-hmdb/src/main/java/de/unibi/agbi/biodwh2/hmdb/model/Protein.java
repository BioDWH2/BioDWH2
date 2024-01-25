package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.hmdb.etl.HMDBGraphExporter;

import java.util.List;

@GraphNodeLabel(HMDBGraphExporter.PROTEIN_LABEL)
public class Protein {
    @GraphProperty("version")
    public String version;
    @JsonProperty("creation_date")
    @GraphProperty("creation_date")
    public String creationDate;
    @JsonProperty("update_date")
    @GraphProperty("update_date")
    public String updateDate;
    @GraphProperty("accession")
    public String accession;
    @GraphProperty("name")
    public String name;
    @JacksonXmlElementWrapper(localName = "secondary_accessions")
    @GraphProperty("secondary_accessions")
    public List<String> secondaryAccessions;
    @JsonProperty("protein_type")
    @GraphProperty("protein_type")
    public String proteinType;
    @JacksonXmlElementWrapper(localName = "synonyms")
    @GraphProperty("synonyms")
    public List<String> synonyms;
    @JsonProperty("gene_name")
    @GraphProperty("gene_name")
    public String geneName;
    @JsonProperty("general_function")
    @GraphProperty("general_function")
    public String generalFunction;
    @JsonProperty("specific_function")
    @GraphProperty("specific_function")
    public String specificFunction;
    @JacksonXmlElementWrapper(localName = "pathways")
    public List<Pathway> pathways;
    @SuppressWarnings("unused")
    @JacksonXmlElementWrapper(localName = "metabolite_associations")
    public List<MetaboliteAssociation> metaboliteAssociations;
    @JacksonXmlElementWrapper(localName = "go_classifications")
    public List<GOClass> goClassifications;
    @JacksonXmlElementWrapper(localName = "subcellular_locations")
    @GraphProperty("subcellular_locations")
    public List<String> subcellularLocations;
    @JsonProperty("gene_properties")
    public GeneProperties geneProperties;
    @JsonProperty("protein_properties")
    public ProteinProperties proteinProperties;
    @JsonProperty("genbank_protein_id")
    @GraphProperty("genbank_protein_id")
    public String genbankProteinId;
    @JsonProperty("uniprot_id")
    @GraphProperty("uniprot_id")
    public String uniprotId;
    @JsonProperty("uniprot_name")
    @GraphProperty("uniprot_name")
    public String uniprotName;
    @JacksonXmlElementWrapper(localName = "pdb_ids")
    @GraphProperty("pdb_ids")
    public List<String> pdbIds;
    @JsonProperty("genbank_gene_id")
    @GraphProperty("genbank_gene_id")
    public String genbankGeneId;
    @JsonProperty("genecard_id")
    @GraphProperty("genecard_id")
    public String genecardId;
    @JsonProperty("geneatlas_id")
    @GraphProperty("geneatlas_id")
    public String geneatlasId;
    @JsonProperty("hgnc_id")
    @GraphProperty("hgnc_id")
    public String hgncId;
    @JacksonXmlElementWrapper(localName = "general_references")
    public List<Reference> generalReferences;
    @JacksonXmlElementWrapper(localName = "metabolite_references")
    public List<MetaboliteReference> metaboliteReferences;
}
