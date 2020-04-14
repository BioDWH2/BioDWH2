package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.List;

public abstract class KeggHierarchicalEntry extends KeggEntry {
    public static class ParentChildRelation {
        public NameIdsPair parent;
        public NameIdsPair child;
    }

    public final List<ParentChildRelation> classes = new ArrayList<>();
    public final List<ParentChildRelation> members = new ArrayList<>();
}
