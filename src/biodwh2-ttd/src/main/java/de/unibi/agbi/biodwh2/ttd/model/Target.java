package de.unibi.agbi.biodwh2.ttd.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphNodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;

@GraphNodeLabel("Target")
public class Target {
    @GraphProperty("id")
    public String id;
    @GraphProperty("previous_id")
    public String formerTargetId;
    @GraphArrayProperty(value = "uniprot_ids", arrayDelimiter = "; ")
    public String uniProtID;
    @GraphProperty("name")
    public String name;
    @GraphArrayProperty(value = "gene_names", arrayDelimiter = "; ")
    public String geneName;
    @GraphProperty("type")
    public String type;
    @GraphProperty("synonyms")
    public String[] synonyms;
    @GraphProperty("function")
    public String function;
    @GraphProperty("pdb_structures")
    public String[] pdbStructures;
    @GraphProperty("ec_number")
    public String ecNumber;
    @GraphProperty("sequence")
    public String sequence;
    @GraphProperty("biochemical_class")
    public String biochemicalClass;
}
