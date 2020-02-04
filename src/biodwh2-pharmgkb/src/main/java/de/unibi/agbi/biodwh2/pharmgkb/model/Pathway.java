package de.unibi.agbi.biodwh2.pharmgkb.model;

import com.univocity.parsers.annotations.Parsed;

public class Pathway {
    @Parsed(field = "From")
    public String from;
    @Parsed(field = "To")
    public String to;
    @Parsed(field = "Reaction Type")
    public String reactionType;
    @Parsed(field = "Controller")
    public String controller;
    @Parsed(field = "Control Type")
    public String controlType;
    @Parsed(field = "Cell Type")
    public String cellType;
    @Parsed(field = "PMIDs")
    public String pmids;
    @Parsed(field = "Genes")
    public String genes;
    @Parsed(field = "Drugs")
    public String drugs;
    @Parsed(field = "Diseases")
    public String diseases;
}
