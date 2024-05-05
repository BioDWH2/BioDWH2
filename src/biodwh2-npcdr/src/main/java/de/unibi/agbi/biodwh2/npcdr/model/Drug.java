package de.unibi.agbi.biodwh2.npcdr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.npcdr.etl.NPCDRGraphExporter;

@JsonPropertyOrder({
        "Drug ID", "NP Name", "PubChem CID", "Synonymous", "Molecule Type", "InChI", "InChIKey", "Canonical SMILES",
        "Formula", "CAS Number", "CHEBI", "HERB ID", "SymMap ID", "TCMSP ID", "TTD ID", "DrugBank ID", "ETMC ID", "GDSC"
})
@GraphNodeLabel(NPCDRGraphExporter.DRUG_LABEL)
public class Drug {
    @JsonProperty("Drug ID")
    @GraphProperty(GraphExporter.ID_KEY)
    public String id;
    @JsonProperty("NP Name")
    @GraphProperty("name")
    public String name;
    @JsonProperty("PubChem CID")
    public String pubChemCID;
    @JsonProperty("Synonymous")
    @GraphArrayProperty(value = "synonyms", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String synonyms;
    @JsonProperty("Molecule Type")
    @GraphProperty(value = "molecule_type", emptyPlaceholder = ".")
    public String moleculeType;
    @JsonProperty("InChI")
    @GraphProperty(value = "inchi", emptyPlaceholder = ".")
    public String inchi;
    @JsonProperty("InChIKey")
    @GraphProperty(value = "inchi_key", emptyPlaceholder = ".")
    public String inchiKey;
    @JsonProperty("Canonical SMILES")
    @GraphProperty(value = "canonical_smiles", emptyPlaceholder = ".")
    public String canonicalSMILES;
    @JsonProperty("Formula")
    @GraphProperty(value = "formula", emptyPlaceholder = ".")
    public String formula;
    @JsonProperty("CAS Number")
    @GraphProperty(value = "cas_number", emptyPlaceholder = ".")
    public String casNumber;
    @JsonProperty("CHEBI")
    public String chebi;
    @JsonProperty("HERB ID")
    @GraphProperty(value = "herb_id", emptyPlaceholder = ".")
    public String herbId;
    @JsonProperty("SymMap ID")
    @GraphProperty(value = "symmap_id", emptyPlaceholder = ".")
    public String symMapId;
    @JsonProperty("TCMSP ID")
    @GraphProperty(value = "tcmsp_id", emptyPlaceholder = ".")
    public String tcmspId;
    @JsonProperty("TTD ID")
    @GraphProperty(value = "ttd_id", emptyPlaceholder = ".")
    public String ttdId;
    @JsonProperty("DrugBank ID")
    @GraphProperty(value = "drugbank_id", emptyPlaceholder = ".")
    public String drugBankId;
    @JsonProperty("ETMC ID")
    @GraphProperty(value = "etmc_id", emptyPlaceholder = ".")
    public String etmcId;
    @JsonProperty("GDSC")
    @GraphProperty(value = "gdsc", emptyPlaceholder = ".")
    public String gdsc;
}
