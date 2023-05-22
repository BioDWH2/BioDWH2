package de.unibi.agbi.biodwh2.hmdb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Metabolite {
    public String version;
    @JsonProperty("creation_date")
    public String creationDate;
    @JsonProperty("update_date")
    public String updateDate;
    public String accession;
    public String status;
    @JacksonXmlElementWrapper(localName = "secondary_accessions")
    public List<String> secondaryAccessions;
    public String name;
    public String description;
    @JacksonXmlElementWrapper(localName = "synonyms")
    @JacksonXmlProperty(localName = "synonym")
    public List<String> synonyms;
    @JsonProperty("chemical_formula")
    public String chemicalFormula;
    @JsonProperty("average_molecular_weight")
    public String averageMolecularWeight;
    @JsonProperty("monisotopic_molecular_weight")
    public String monisotopicMolecularWeight;
    @JsonProperty("iupac_name")
    public String iupacName;
    @JsonProperty("traditional_iupac")
    public String traditionalIupac;
    @JsonProperty("cas_registry_number")
    public String casRegistryNumber;
    public String smiles;
    public String inchi;
    public String inchikey;
    public Taxonomy taxonomy;
    @JacksonXmlElementWrapper(localName = "ontology")
    public List<OntologyTerm> ontology;
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
    public Integer chemspiderId;
    @JsonProperty("drugbank_id")
    public String drugbankId;
    @JsonProperty("foodb_id")
    public String foodbId;
    @JsonProperty("pubchem_compound_id")
    public Integer pubchemCompoundId;
    @JsonProperty("pdb_id")
    public String pdbId;
    @JsonProperty("chebi_id")
    public Integer chebiId;
    @JsonProperty("phenol_explorer_compound_id")
    public String phenolExplorerCompoundId;
    @JsonProperty("knapsack_id")
    public String knapsackId;
    @JsonProperty("kegg_id")
    public String keggId;
    @JsonProperty("biocyc_id")
    public String biocycId;
    @JsonProperty("bigg_id")
    public String biggId;
    @JsonProperty("wikipedia_id")
    public String wikipediaId;
    @JsonProperty("metlin_id")
    public Integer metlinId;
    @JsonProperty("vmh_id")
    public String vmhId;
    @JsonProperty("fbonto_id")
    public String fbontoId;
    @JsonProperty("synthesis_reference")
    public String synthesisReference;
    @JacksonXmlElementWrapper(localName = "general_references")
    public List<Reference> generalReferences;
    @JacksonXmlElementWrapper(localName = "protein_associations")
    public List<Protein> proteinAssociations;
}
