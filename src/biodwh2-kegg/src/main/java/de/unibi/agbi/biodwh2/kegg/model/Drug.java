package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.List;

public class Drug extends KeggEntry {
    public String formula;
    public String exactMass;
    public String molecularWeight;
    public String atoms;
    public String bonds;
    public String bracket;
    public String nameAbbreviation;
    public String efficacy;
    public final List<NameIdsPair> efficacyDiseases = new ArrayList<>();
    public final List<ParentChildRelation> classes = new ArrayList<>();
    public final List<Sequence> sequences = new ArrayList<>();
    public final List<Interaction> interactions = new ArrayList<>();
    public final List<Metabolism> metabolisms = new ArrayList<>();
    public final List<NameIdsPair> targets = new ArrayList<>();
    public final List<NameIdsPair> networkTargets = new ArrayList<>();
    public final List<NameIdsPair> sources = new ArrayList<>();
    public final List<List<NameIdsPair>> mixtures = new ArrayList<>();
}
