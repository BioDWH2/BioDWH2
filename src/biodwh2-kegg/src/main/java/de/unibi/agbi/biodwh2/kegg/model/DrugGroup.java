package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.List;

public class DrugGroup extends KeggEntry {
    public final List<ParentChildRelation> classes = new ArrayList<>();
    public final List<ParentChildRelation> members = new ArrayList<>();
    public final List<String> nameStems = new ArrayList<>();
    public String nameAbbreviation;
}
