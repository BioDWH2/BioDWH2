package de.unibi.agbi.biodwh2.kegg.model;

import java.util.List;

public class Drug extends KeggEntry {
    public String formula;
    public List<String> sequences;
    public String exactMass;
    public String molecularWeight;
}
