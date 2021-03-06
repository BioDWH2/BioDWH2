package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.List;

public class Drug extends KeggHierarchicalEntry {
    public String formula;
    public final List<Sequence> sequences = new ArrayList<>();
    public String exactMass;
    public String molecularWeight;
    public String atoms;
    public String bonds;
    public final List<Interaction> interactions = new ArrayList<>();
    public final List<Metabolism> metabolisms = new ArrayList<>();
    public final List<NameIdsPair> targets = new ArrayList<>();
    public final List<NameIdsPair> networkTargets = new ArrayList<>();
    public String efficacy;
    public final List<NameIdsPair> efficacyDiseases = new ArrayList<>();
    public final List<NameIdsPair> sources = new ArrayList<>();
    public final List<List<NameIdsPair>> mixtures = new ArrayList<>();
    public Bracket bracket;
    public String nameAbbreviation;
}
