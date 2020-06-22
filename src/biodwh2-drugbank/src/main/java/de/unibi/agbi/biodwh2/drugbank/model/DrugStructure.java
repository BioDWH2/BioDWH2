package de.unibi.agbi.biodwh2.drugbank.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabels;

@NodeLabels({"Drug"})
public class DrugStructure {
    @GraphProperty("id")
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
    @GraphProperty("iupac")
    public String iupac;
    @GraphProperty("iupac_traditional")
    public String traditionalIupac;
    public DrugbankMetaboliteId drugbankId;
    @GraphProperty("name")
    public String name;
}
