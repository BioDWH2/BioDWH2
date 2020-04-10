package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.List;

public class Disease extends KeggEntry {
    public String description;
    public List<NameIdsPair> envFactors = new ArrayList<>();
    public List<NameIdsPair> carcinogens = new ArrayList<>();
    public List<NameIdsPair> drugs = new ArrayList<>();
}
