package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;

@NodeLabel("PathwayStep")
public class Pathway {
    @Parsed(field = "From")
    @GraphProperty("from")
    public String from;
    @Parsed(field = "To")
    @GraphProperty("to")
    public String to;
    @Parsed(field = "Reaction Type")
    @GraphProperty("reaction_type")
    public String reactionType;
    @Parsed(field = "Controller")
    @GraphProperty("controller")
    public String controller;
    @Parsed(field = "Control Type")
    @GraphProperty("control_type")
    public String controlType;
    @Parsed(field = "Cell Type")
    @GraphProperty("cell_type")
    public String cellType;
    @Parsed(field = "PMIDs")
    public String pmids;
    @Parsed(field = "Genes")
    @GraphProperty("genes")
    public String genes;
    @Parsed(field = "Drugs")
    @GraphProperty("drugs")
    public String drugs;
    @Parsed(field = "Diseases")
    @GraphProperty("diseases")
    public String diseases;
}
