package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public final class Reaction {
    public String sequence;
    @JsonProperty("left-element")
    public ReactionElement leftElement;
    @JsonProperty("right-element")
    public ReactionElement rightElement;
    public ArrayList<ReactionEnzyme> enzymes;
}
