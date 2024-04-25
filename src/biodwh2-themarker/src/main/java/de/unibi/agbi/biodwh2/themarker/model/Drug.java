package de.unibi.agbi.biodwh2.themarker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNumberProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.themarker.etl.TheMarkerGraphExporter;

@JsonPropertyOrder({
        "Drug ID", "Drug Name", "Drug Class", "Synonymous", "Drug Status", "Drug Status Class", "PubChem CID",
        "DrugBank ID", "TTD Drug ID", "Molecular Weight", "Polararea", "Complexity", "xlogp", "Heavycnt", "Hbonddonor",
        "Hbondacc", "Rotbonds", "Formula", "iupacname", "canonicalsmiles", "isosmiles", "InChI", "InChIKey"
})
@GraphNodeLabel(TheMarkerGraphExporter.DRUG_LABEL)
public class Drug {
    @JsonProperty("Drug ID")
    @GraphProperty(GraphExporter.ID_KEY)
    public String id;
    @JsonProperty("Drug Name")
    @GraphProperty(value = "name", emptyPlaceholder = ".")
    public String name;
    @JsonProperty("Drug Class")
    @GraphProperty(value = "class", emptyPlaceholder = ".")
    public String _class;
    @JsonProperty("Synonymous")
    @GraphArrayProperty(value = "synonyms", arrayDelimiter = "; ", emptyPlaceholder = ".")
    public String synonyms;
    @JsonProperty("Drug Status")
    @GraphProperty(value = "status", emptyPlaceholder = ".")
    public String status;
    @JsonProperty("Drug Status Class")
    @GraphProperty(value = "status_class", emptyPlaceholder = ".")
    public String statusClass;
    @JsonProperty("PubChem CID")
    @GraphNumberProperty(value = "pubchem_cid", emptyPlaceholder = ".")
    public String pubChemCID;
    @JsonProperty("DrugBank ID")
    @GraphProperty(value = "drugbank_id", emptyPlaceholder = ".")
    public String drugBankId;
    @JsonProperty("TTD Drug ID")
    @GraphProperty(value = "ttd_drug_id", emptyPlaceholder = ".")
    public String ttdDrugId;
    @JsonProperty("Molecular Weight")
    @GraphProperty(value = "molecular_weight", emptyPlaceholder = ".")
    public String molecularWeight;
    @JsonProperty("Polararea")
    @GraphProperty(value = "polarea", emptyPlaceholder = ".")
    public String polarea;
    @JsonProperty("Complexity")
    @GraphProperty(value = "complexity", emptyPlaceholder = ".")
    public String complexity;
    @JsonProperty("xlogp")
    @GraphProperty(value = "xlogp", emptyPlaceholder = ".")
    public String xlogp;
    @JsonProperty("Heavycnt")
    @GraphNumberProperty(value = "heavycnt", emptyPlaceholder = ".")
    public String heavyCount;
    @JsonProperty("Hbonddonor")
    @GraphNumberProperty(value = "hbonddonor", emptyPlaceholder = ".")
    public String hBondDonor;
    @JsonProperty("Hbondacc")
    @GraphNumberProperty(value = "hbondacc", emptyPlaceholder = ".")
    public String hBondAcceptor;
    @JsonProperty("Rotbonds")
    @GraphNumberProperty(value = "rotbonds", emptyPlaceholder = ".")
    public String rotBonds;
    @JsonProperty("Formula")
    @GraphProperty(value = "formula", emptyPlaceholder = ".")
    public String formula;
    @JsonProperty("iupacname")
    @GraphProperty(value = "iupac_name", emptyPlaceholder = ".")
    public String iupacName;
    @JsonProperty("canonicalsmiles")
    @GraphProperty(value = "canonical_smiles", emptyPlaceholder = ".")
    public String canonicalSmiles;
    @JsonProperty("isosmiles")
    @GraphProperty(value = "iso_smiles", emptyPlaceholder = ".")
    public String isoSmiles;
    @JsonProperty("InChI")
    @GraphProperty(value = "inchi", emptyPlaceholder = ".")
    public String inchi;
    @JsonProperty("InChIKey")
    @GraphProperty(value = "inchi_key", emptyPlaceholder = ".")
    public String inchiKey;
}
