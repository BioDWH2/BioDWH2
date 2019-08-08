package de.unibi.agbi.biodwh2.drugbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public final class Pathway {
    @JsonProperty("smpdb-id")
    public String smpdbId;
    public String name;
    public String category;
    public ArrayList<PathwayDrug> drugs;
    public ArrayList<String> enzymes;
}
