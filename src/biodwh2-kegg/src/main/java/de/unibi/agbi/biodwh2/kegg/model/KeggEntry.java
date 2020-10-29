package de.unibi.agbi.biodwh2.kegg.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class KeggEntry {
    public String id;
    public final List<String> tags = new ArrayList<>();
    public final List<String> names = new ArrayList<>();
    public final Set<String> externalIds = new HashSet<>();
    public final List<Reference> references = new ArrayList<>();
    public final List<String> comments = new ArrayList<>();
    public final List<String> remarks = new ArrayList<>();
}
