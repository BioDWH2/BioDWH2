package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.List;

public class Network extends KeggEntry {
    public String type;
    public String definition;
    public String expandedDefinition;
    public final List<NameIdsPair> genes = new ArrayList<>();
    public final List<NameIdsPair> variants = new ArrayList<>();
    public final List<NameIdsPair> diseases = new ArrayList<>();
    public final List<NameIdsPair> members = new ArrayList<>();
    public final List<NameIdsPair> perturbants = new ArrayList<>();
    public final List<NameIdsPair> classes = new ArrayList<>();
    public final List<NameIdsPair> metabolites = new ArrayList<>();
}
