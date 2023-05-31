package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.ValueTransformation;

import java.util.List;

public class Metabolite {
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
    @GraphProperty("status")
    public String status;
    @JacksonXmlElementWrapper(localName = "secondary_accessions")
    @GraphProperty(value = "secondary_accessions", transformation = ValueTransformation.COLLECTION_TO_ARRAY)
    public List<String> secondaryAccessions;
    @GraphProperty("name")
    public String name;
    @GraphProperty("description")
    public String description;
    @JacksonXmlElementWrapper(localName = "synonyms")
    @JacksonXmlProperty(localName = "synonym")
    @GraphProperty(value = "synonyms", transformation = ValueTransformation.COLLECTION_TO_ARRAY)
    public List<String> synonyms;
    @JsonProperty("chemical_formula")
    @GraphProperty("chemical_formula")
    public String chemicalFormula;
    @JsonProperty("average_molecular_weight")
    @GraphProperty("average_molecular_weight")
    public String averageMolecularWeight;
    @JsonProperty("monisotopic_molecular_weight")
    @GraphProperty("monisotopic_molecular_weight")
    public String monisotopicMolecularWeight;
    @JsonProperty("iupac_name")
    @GraphProperty("iupac_name")
    public String iupacName;
    @JsonProperty("traditional_iupac")
    @GraphProperty("traditional_iupac")
    public String traditionalIupac;
    @JsonProperty("cas_registry_number")
    @GraphProperty("cas_registry_number")
    public String casRegistryNumber;
    @GraphProperty("smiles")
    public String smiles;
    @GraphProperty("inchi")
    public String inchi;
    @GraphProperty("inchi_key")
    public String inchikey;
    public Taxonomy taxonomy;
    @JacksonXmlElementWrapper(localName = "ontology")
    public List<OntologyTerm> ontology;
    @GraphProperty("state")
    public String state;
    @JacksonXmlElementWrapper(localName = "experimental_properties")
    public List<Property> experimentalProperties;
    @JacksonXmlElementWrapper(localName = "predicted_properties")
    public List<Property> predictedProperties;
    @JacksonXmlElementWrapper(localName = "spectra")
    public List<Spectrum> spectra;
    @JsonProperty("biological_properties")
    public BiologicalProperties biologicalProperties;
    @JacksonXmlElementWrapper(localName = "normal_concentrations")
    public List<Concentration> normalConcentrations;
    @JacksonXmlElementWrapper(localName = "abnormal_concentrations")
    public List<Concentration> abnormalConcentrations;
    @JacksonXmlElementWrapper(localName = "diseases")
    public List<Disease> diseases;
    @JsonProperty("chemspider_id")
    @GraphProperty("chemspider_id")
    public Integer chemspiderId;
    @JsonProperty("drugbank_id")
    @GraphProperty("drugbank_id")
    public String drugbankId;
    @JsonProperty("foodb_id")
    @GraphProperty("foodb_id")
    public String foodbId;
    @JsonProperty("pubchem_compound_id")
    @GraphProperty("pubchem_compound_id")
    public Integer pubchemCompoundId;
    @JsonProperty("pdb_id")
    @GraphProperty("pdb_id")
    public String pdbId;
    @JsonProperty("chebi_id")
    @GraphProperty("chebi_id")
    public Integer chebiId;
    @JsonProperty("phenol_explorer_compound_id")
    @GraphProperty("phenol_explorer_compound_id")
    public String phenolExplorerCompoundId;
    @JsonProperty("knapsack_id")
    @GraphProperty("knapsack_id")
    public String knapsackId;
    @JsonProperty("kegg_id")
    @GraphProperty("kegg_id")
    public String keggId;
    @JsonProperty("biocyc_id")
    @GraphProperty("biocyc_id")
    public String biocycId;
    @JsonProperty("bigg_id")
    @GraphProperty("bigg_id")
    public String biggId;
    @JsonProperty("wikipedia_id")
    @GraphProperty("wikipedia_id")
    public String wikipediaId;
    @JsonProperty("metlin_id")
    @GraphProperty("metlin_id")
    public Integer metlinId;
    @JsonProperty("vmh_id")
    @GraphProperty("vmh_id")
    public String vmhId;
    @JsonProperty("fbonto_id")
    @GraphProperty("fbonto_id")
    public String fbontoId;
    @JsonProperty("synthesis_reference")
    @GraphProperty("synthesis_reference")
    public String synthesisReference;
    @JacksonXmlElementWrapper(localName = "general_references")
    public List<Reference> generalReferences;
    @JacksonXmlElementWrapper(localName = "protein_associations")
    public List<ProteinAssociation> proteinAssociations;
}
