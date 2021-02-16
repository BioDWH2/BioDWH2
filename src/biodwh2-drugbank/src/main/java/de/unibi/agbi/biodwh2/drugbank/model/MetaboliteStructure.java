package de.unibi.agbi.biodwh2.drugbank.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"Metabolite"})
public class MetaboliteStructure {
    public String databaseId;
    public String databaseName;
    @GraphProperty("smiles")
    public String smiles;
    @GraphProperty("inchi")
    public String inchiId;
    @GraphProperty("inchi_key")
    public String inchiKey;
    @GraphProperty("formula")
    public String formula;
    @GraphProperty("molecular_weight")
    public String molecularWeight;
    @GraphProperty("exact_mass")
    public String exactMass;
    @GraphProperty("jchem_iupac")
    public String iupac;
    @GraphProperty("jchem_traditional_iupac")
    public String traditionalIupac;
    @GraphProperty("drugbank_id")
    public String drugbankId;
    @GraphProperty("name")
    public String name;
    @GraphProperty("unii")
    public String unii;
    @GraphProperty("jchem_rule_of_five")
    public String ruleOfFive;
    @GraphProperty("jchem_ghose_filter")
    public String ghoseFilter;
    @GraphProperty("jchem_veber_rule")
    public String veberRule;
    @GraphProperty("jchem_mddr_like_rule")
    public String mddrLikeRule;
}
