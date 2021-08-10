package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.List;

public class Disease extends KeggEntry {
    public String description;
    public final List<NameIdsPair> envFactors = new ArrayList<>();
    public final List<NameIdsPair> carcinogens = new ArrayList<>();
    public final List<NameIdsPair> pathogens = new ArrayList<>();
    public final List<NameIdsPair> pathogenModules = new ArrayList<>();
    public final List<NetworkLink> networks = new ArrayList<>();
    public final List<NameIdsPair> drugs = new ArrayList<>();
    public final List<NameIdsPair> genes = new ArrayList<>();
    public final List<String> categories = new ArrayList<>();
    public final List<NameIdsPair> superGroups = new ArrayList<>();
    public final List<NameIdsPair> subGroups = new ArrayList<>();
    public final List<NameIdsPair> relPathways = new ArrayList<>();
}
